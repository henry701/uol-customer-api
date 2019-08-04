package br.com.henry.selective.uol.customer.test;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

/**
 * Needed because the IpVigilante certificate was not being recognized as valid for HTTPS requests. Is very convenient for the integration
 * test, where security is not a concern because if any data is modified, the integration test will fail, nothing else.
 *
 * @link https://stackoverflow.com/questions/23504819/how-to-disable-ssl-certificate-checking-with-spring-resttemplate/24491820
 */
public final class SSLUtil {

    private static final TrustManager[] UNQUESTIONING_TRUST_MANAGER = new TrustManager[] {
        new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
    };

    public static void turnOffSslChecking() throws NoSuchAlgorithmException, KeyManagementException {
        // Install the all-trusting trust manager
        final SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, UNQUESTIONING_TRUST_MANAGER, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }

    public static void turnOnSslChecking() throws KeyManagementException, NoSuchAlgorithmException {
        // Return it to the initial state (discovered by reflection, now hardcoded)
        SSLContext.getInstance("SSL").init(null, null, null);
    }

    private SSLUtil() {
        throw new UnsupportedOperationException("Do not instantiate libraries.");
    }
}