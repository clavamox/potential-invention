package com.belon.http;

import com.belon.http.NanoHTTPD;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public class RouterNanoHTTPD extends NanoHTTPD {
    private static final Logger LOG = Logger.getLogger(RouterNanoHTTPD.class.getName());
    private UriRouter router;

    public interface UriResponder {
        NanoHTTPD.Response delete(UriResource uriResource, Map<String, String> map, NanoHTTPD.IHTTPSession iHTTPSession);

        NanoHTTPD.Response get(UriResource uriResource, Map<String, String> map, NanoHTTPD.IHTTPSession iHTTPSession);

        NanoHTTPD.Response other(String str, UriResource uriResource, Map<String, String> map, NanoHTTPD.IHTTPSession iHTTPSession);

        NanoHTTPD.Response post(UriResource uriResource, Map<String, String> map, NanoHTTPD.IHTTPSession iHTTPSession);

        NanoHTTPD.Response put(UriResource uriResource, Map<String, String> map, NanoHTTPD.IHTTPSession iHTTPSession);
    }

    public static abstract class DefaultStreamHandler implements UriResponder {
        public abstract InputStream getData();

        public abstract String getMimeType();

        public abstract NanoHTTPD.Response.IStatus getStatus();

        @Override // com.belon.http.RouterNanoHTTPD.UriResponder
        public NanoHTTPD.Response get(UriResource uriResource, Map<String, String> map, NanoHTTPD.IHTTPSession iHTTPSession) {
            return NanoHTTPD.newChunkedResponse(getStatus(), getMimeType(), getData());
        }

        @Override // com.belon.http.RouterNanoHTTPD.UriResponder
        public NanoHTTPD.Response post(UriResource uriResource, Map<String, String> map, NanoHTTPD.IHTTPSession iHTTPSession) {
            return get(uriResource, map, iHTTPSession);
        }

        @Override // com.belon.http.RouterNanoHTTPD.UriResponder
        public NanoHTTPD.Response put(UriResource uriResource, Map<String, String> map, NanoHTTPD.IHTTPSession iHTTPSession) {
            return get(uriResource, map, iHTTPSession);
        }

        @Override // com.belon.http.RouterNanoHTTPD.UriResponder
        public NanoHTTPD.Response delete(UriResource uriResource, Map<String, String> map, NanoHTTPD.IHTTPSession iHTTPSession) {
            return get(uriResource, map, iHTTPSession);
        }

        @Override // com.belon.http.RouterNanoHTTPD.UriResponder
        public NanoHTTPD.Response other(String str, UriResource uriResource, Map<String, String> map, NanoHTTPD.IHTTPSession iHTTPSession) {
            return get(uriResource, map, iHTTPSession);
        }
    }

    public static abstract class DefaultHandler extends DefaultStreamHandler {
        @Override // com.belon.http.RouterNanoHTTPD.DefaultStreamHandler
        public abstract NanoHTTPD.Response.IStatus getStatus();

        public abstract String getText();

        @Override // com.belon.http.RouterNanoHTTPD.DefaultStreamHandler, com.belon.http.RouterNanoHTTPD.UriResponder
        public NanoHTTPD.Response get(UriResource uriResource, Map<String, String> map, NanoHTTPD.IHTTPSession iHTTPSession) {
            return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), getText());
        }

        @Override // com.belon.http.RouterNanoHTTPD.DefaultStreamHandler
        public InputStream getData() {
            throw new IllegalStateException("this method should not be called in a text based nanolet");
        }
    }

    public static class GeneralHandler extends DefaultHandler {
        @Override // com.belon.http.RouterNanoHTTPD.DefaultStreamHandler
        public String getMimeType() {
            return NanoHTTPD.MIME_HTML;
        }

        @Override // com.belon.http.RouterNanoHTTPD.DefaultHandler
        public String getText() {
            throw new IllegalStateException("this method should not be called");
        }

        @Override // com.belon.http.RouterNanoHTTPD.DefaultHandler, com.belon.http.RouterNanoHTTPD.DefaultStreamHandler
        public NanoHTTPD.Response.IStatus getStatus() {
            return NanoHTTPD.Response.Status.OK;
        }

        @Override // com.belon.http.RouterNanoHTTPD.DefaultHandler, com.belon.http.RouterNanoHTTPD.DefaultStreamHandler, com.belon.http.RouterNanoHTTPD.UriResponder
        public NanoHTTPD.Response get(UriResource uriResource, Map<String, String> map, NanoHTTPD.IHTTPSession iHTTPSession) {
            StringBuilder sb = new StringBuilder("<html><body><h1>Url: ");
            sb.append(iHTTPSession.getUri());
            sb.append("</h1><br>");
            Map<String, String> parms = iHTTPSession.getParms();
            if (parms.size() > 0) {
                for (Map.Entry<String, String> entry : parms.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    sb.append("<p>Param '");
                    sb.append(key);
                    sb.append("' = ");
                    sb.append(value);
                    sb.append("</p>");
                }
            } else {
                sb.append("<p>no params in url</p><br>");
            }
            return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), sb.toString());
        }
    }

    public static class StaticPageHandler extends DefaultHandler {
        private static String[] getPathArray(String str) {
            String[] split = str.split("/");
            ArrayList arrayList = new ArrayList();
            for (String str2 : split) {
                if (str2.length() > 0) {
                    arrayList.add(str2);
                }
            }
            return (String[]) arrayList.toArray(new String[0]);
        }

        @Override // com.belon.http.RouterNanoHTTPD.DefaultHandler
        public String getText() {
            throw new IllegalStateException("this method should not be called");
        }

        @Override // com.belon.http.RouterNanoHTTPD.DefaultStreamHandler
        public String getMimeType() {
            throw new IllegalStateException("this method should not be called");
        }

        @Override // com.belon.http.RouterNanoHTTPD.DefaultHandler, com.belon.http.RouterNanoHTTPD.DefaultStreamHandler
        public NanoHTTPD.Response.IStatus getStatus() {
            return NanoHTTPD.Response.Status.OK;
        }

        @Override // com.belon.http.RouterNanoHTTPD.DefaultHandler, com.belon.http.RouterNanoHTTPD.DefaultStreamHandler, com.belon.http.RouterNanoHTTPD.UriResponder
        public NanoHTTPD.Response get(UriResource uriResource, Map<String, String> map, NanoHTTPD.IHTTPSession iHTTPSession) {
            String uri = uriResource.getUri();
            String normalizeUri = RouterNanoHTTPD.normalizeUri(iHTTPSession.getUri());
            int i = 0;
            int i2 = 0;
            while (true) {
                if (i2 >= Math.min(uri.length(), normalizeUri.length())) {
                    break;
                }
                if (uri.charAt(i2) != normalizeUri.charAt(i2)) {
                    normalizeUri = RouterNanoHTTPD.normalizeUri(normalizeUri.substring(i2));
                    break;
                }
                i2++;
            }
            File file = (File) uriResource.initParameter(File.class);
            String[] pathArray = getPathArray(normalizeUri);
            int length = pathArray.length;
            while (i < length) {
                File file2 = new File(file, pathArray[i]);
                i++;
                file = file2;
            }
            if (file.isDirectory()) {
                File file3 = new File(file, "index.html");
                file = !file3.exists() ? new File(file3.getParentFile(), "index.htm") : file3;
            }
            if (!file.exists() || !file.isFile()) {
                return new Error404UriHandler().get(uriResource, map, iHTTPSession);
            }
            try {
                return NanoHTTPD.newChunkedResponse(getStatus(), NanoHTTPD.getMimeTypeForFile(file.getName()), fileToInputStream(file));
            } catch (IOException unused) {
                return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.REQUEST_TIMEOUT, NanoHTTPD.MIME_PLAINTEXT, null);
            }
        }

        protected BufferedInputStream fileToInputStream(File file) throws IOException {
            return new BufferedInputStream(new FileInputStream(file));
        }
    }

    public static class Error404UriHandler extends DefaultHandler {
        @Override // com.belon.http.RouterNanoHTTPD.DefaultStreamHandler
        public String getMimeType() {
            return NanoHTTPD.MIME_HTML;
        }

        @Override // com.belon.http.RouterNanoHTTPD.DefaultHandler
        public String getText() {
            return "<html><body><h3>Error 404: the requested page doesn't exist.</h3></body></html>";
        }

        @Override // com.belon.http.RouterNanoHTTPD.DefaultHandler, com.belon.http.RouterNanoHTTPD.DefaultStreamHandler
        public NanoHTTPD.Response.IStatus getStatus() {
            return NanoHTTPD.Response.Status.NOT_FOUND;
        }
    }

    public static class IndexHandler extends DefaultHandler {
        @Override // com.belon.http.RouterNanoHTTPD.DefaultStreamHandler
        public String getMimeType() {
            return NanoHTTPD.MIME_HTML;
        }

        @Override // com.belon.http.RouterNanoHTTPD.DefaultHandler
        public String getText() {
            return "<html><body><h2>Hello world!</h3></body></html>";
        }

        @Override // com.belon.http.RouterNanoHTTPD.DefaultHandler, com.belon.http.RouterNanoHTTPD.DefaultStreamHandler
        public NanoHTTPD.Response.IStatus getStatus() {
            return NanoHTTPD.Response.Status.OK;
        }
    }

    public static class NotImplementedHandler extends DefaultHandler {
        @Override // com.belon.http.RouterNanoHTTPD.DefaultStreamHandler
        public String getMimeType() {
            return NanoHTTPD.MIME_HTML;
        }

        @Override // com.belon.http.RouterNanoHTTPD.DefaultHandler
        public String getText() {
            return "<html><body><h2>The uri is mapped in the router, but no handler is specified. <br> Status: Not implemented!</h3></body></html>";
        }

        @Override // com.belon.http.RouterNanoHTTPD.DefaultHandler, com.belon.http.RouterNanoHTTPD.DefaultStreamHandler
        public NanoHTTPD.Response.IStatus getStatus() {
            return NanoHTTPD.Response.Status.OK;
        }
    }

    public static String normalizeUri(String str) {
        if (str == null) {
            return str;
        }
        if (str.startsWith("/")) {
            str = str.substring(1);
        }
        return str.endsWith("/") ? str.substring(0, str.length() - 1) : str;
    }

    public static class UriResource {
        private static final String PARAM_MATCHER = "([A-Za-z0-9\\-\\._~:/?#\\[\\]@!\\$&'\\(\\)\\*\\+,;=]+)";
        private final Class<?> handler;
        private final Object[] initParameter;
        private final int priority;
        private final String uri;
        private List<String> uriParams = new ArrayList();
        private final Pattern uriPattern;
        private static final Pattern PARAM_PATTERN = Pattern.compile("(?<=(^|/)):[a-zA-Z0-9_-]+(?=(/|$))");
        private static final Map<String, String> EMPTY = Collections.unmodifiableMap(new HashMap());

        private void parse() {
        }

        public UriResource(String str, int i, Class<?> cls, Object... objArr) {
            this.handler = cls;
            this.initParameter = objArr;
            if (str != null) {
                this.uri = RouterNanoHTTPD.normalizeUri(str);
                parse();
                this.uriPattern = createUriPattern();
            } else {
                this.uriPattern = null;
                this.uri = null;
            }
            this.priority = i + (this.uriParams.size() * 1000);
        }

        private Pattern createUriPattern() {
            String str = this.uri;
            Matcher matcher = PARAM_PATTERN.matcher(str);
            int i = 0;
            while (matcher.find(i)) {
                this.uriParams.add(str.substring(matcher.start() + 1, matcher.end()));
                str = str.substring(0, matcher.start()) + PARAM_MATCHER + str.substring(matcher.end());
                i = matcher.start() + 45;
                matcher = PARAM_PATTERN.matcher(str);
            }
            return Pattern.compile(str);
        }

        public NanoHTTPD.Response process(Map<String, String> map, NanoHTTPD.IHTTPSession iHTTPSession) {
            String str;
            Class<?> cls = this.handler;
            if (cls != null) {
                try {
                    Object newInstance = cls.newInstance();
                    if (newInstance instanceof UriResponder) {
                        UriResponder uriResponder = (UriResponder) newInstance;
                        int i = AnonymousClass1.$SwitchMap$com$belon$http$NanoHTTPD$Method[iHTTPSession.getMethod().ordinal()];
                        if (i == 1) {
                            return uriResponder.get(this, map, iHTTPSession);
                        }
                        if (i == 2) {
                            return uriResponder.post(this, map, iHTTPSession);
                        }
                        if (i == 3) {
                            return uriResponder.put(this, map, iHTTPSession);
                        }
                        if (i == 4) {
                            return uriResponder.delete(this, map, iHTTPSession);
                        }
                        return uriResponder.other(iHTTPSession.getMethod().toString(), this, map, iHTTPSession);
                    }
                    return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, NanoHTTPD.MIME_PLAINTEXT, "Return: " + this.handler.getCanonicalName() + ".toString() -> " + newInstance);
                } catch (Exception e) {
                    str = "Error: " + e.getClass().getName() + " : " + e.getMessage();
                    RouterNanoHTTPD.LOG.log(Level.SEVERE, str, (Throwable) e);
                }
            } else {
                str = "General error!";
            }
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, str);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("UrlResource{uri='");
            String str = this.uri;
            if (str == null) {
                str = "/";
            }
            return sb.append(str).append("', urlParts=").append(this.uriParams).append('}').toString();
        }

        public String getUri() {
            return this.uri;
        }

        public <T> T initParameter(Class<T> cls) {
            return (T) initParameter(0, cls);
        }

        public <T> T initParameter(int i, Class<T> cls) {
            Object[] objArr = this.initParameter;
            if (objArr.length <= i) {
                RouterNanoHTTPD.LOG.severe("init parameter index not available " + i);
                return null;
            }
            return cls.cast(objArr[i]);
        }

        public Map<String, String> match(String str) {
            Matcher matcher = this.uriPattern.matcher(str);
            if (!matcher.matches()) {
                return null;
            }
            if (this.uriParams.size() > 0) {
                HashMap hashMap = new HashMap();
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    hashMap.put(this.uriParams.get(i - 1), matcher.group(i));
                }
                return hashMap;
            }
            return EMPTY;
        }
    }

    /* renamed from: com.belon.http.RouterNanoHTTPD$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$belon$http$NanoHTTPD$Method;

        static {
            int[] iArr = new int[NanoHTTPD.Method.values().length];
            $SwitchMap$com$belon$http$NanoHTTPD$Method = iArr;
            try {
                iArr[NanoHTTPD.Method.GET.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$belon$http$NanoHTTPD$Method[NanoHTTPD.Method.POST.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$belon$http$NanoHTTPD$Method[NanoHTTPD.Method.PUT.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$belon$http$NanoHTTPD$Method[NanoHTTPD.Method.DELETE.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    public static class UriRouter {
        private UriResource error404Url;
        private List<UriResource> mappings = new ArrayList();
        private Class<?> notImplemented;

        public NanoHTTPD.Response process(NanoHTTPD.IHTTPSession iHTTPSession) {
            String normalizeUri = RouterNanoHTTPD.normalizeUri(iHTTPSession.getUri());
            UriResource uriResource = this.error404Url;
            Iterator<UriResource> it = this.mappings.iterator();
            Map<String, String> map = null;
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                UriResource next = it.next();
                Map<String, String> match = next.match(normalizeUri);
                if (match != null) {
                    uriResource = next;
                    map = match;
                    break;
                }
                map = match;
            }
            return uriResource.process(map, iHTTPSession);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addRoute(String str, int i, Class<?> cls, Object... objArr) {
            if (str != null) {
                if (cls != null) {
                    this.mappings.add(new UriResource(str, i + this.mappings.size(), cls, objArr));
                } else {
                    this.mappings.add(new UriResource(str, i + this.mappings.size(), this.notImplemented, new Object[0]));
                }
                sortMappings();
            }
        }

        private void sortMappings() {
            Collections.sort(this.mappings, new Comparator<UriResource>() { // from class: com.belon.http.RouterNanoHTTPD.UriRouter.1
                @Override // java.util.Comparator
                public int compare(UriResource uriResource, UriResource uriResource2) {
                    return uriResource.priority - uriResource2.priority;
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void removeRoute(String str) {
            String normalizeUri = RouterNanoHTTPD.normalizeUri(str);
            Iterator<UriResource> it = this.mappings.iterator();
            while (it.hasNext()) {
                if (normalizeUri.equals(it.next().getUri())) {
                    it.remove();
                    return;
                }
            }
        }

        public void setNotFoundHandler(Class<?> cls) {
            this.error404Url = new UriResource(null, 100, cls, new Object[0]);
        }

        public void setNotImplemented(Class<?> cls) {
            this.notImplemented = cls;
        }
    }

    public RouterNanoHTTPD(int i) {
        super(i);
        this.router = new UriRouter();
    }

    public void addMappings() {
        this.router.setNotImplemented(NotImplementedHandler.class);
        this.router.setNotFoundHandler(Error404UriHandler.class);
        this.router.addRoute("/", 1073741823, IndexHandler.class, new Object[0]);
        this.router.addRoute("/index.html", 1073741823, IndexHandler.class, new Object[0]);
    }

    public void addRoute(String str, Class<?> cls, Object... objArr) {
        this.router.addRoute(str, 100, cls, objArr);
    }

    public void removeRoute(String str) {
        this.router.removeRoute(str);
    }

    @Override // com.belon.http.NanoHTTPD
    public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession iHTTPSession) {
        return this.router.process(iHTTPSession);
    }
}