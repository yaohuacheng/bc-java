package org.bouncycastle.crypto.tls;

import java.io.IOException;
import java.util.Hashtable;

public abstract class DefaultTlsClient extends AbstractTlsClient {

    protected int[] namedCurves;
    protected short[] ecPointFormats;

    public DefaultTlsClient() {
        super();
    }

    public DefaultTlsClient(TlsCipherFactory cipherFactory) {
        super(cipherFactory);
    }

    public int[] getCipherSuites() {
        return new int[] { CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA,
            CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA,
            CipherSuite.TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA,
            CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA,
            CipherSuite.TLS_DHE_RSA_WITH_AES_128_CBC_SHA,
            CipherSuite.TLS_DHE_RSA_WITH_3DES_EDE_CBC_SHA,
            CipherSuite.TLS_RSA_WITH_AES_256_CBC_SHA, CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA,
            CipherSuite.TLS_RSA_WITH_3DES_EDE_CBC_SHA, };
    }

    public Hashtable getClientExtensions() throws IOException {

        Hashtable clientExtensions = super.getClientExtensions();

        if (TlsECCUtils.containsECCCipherSuites(getCipherSuites())) {
            /*
             * RFC 4492 5.1. A client that proposes ECC cipher suites in its ClientHello message
             * appends these extensions (along with any others), enumerating the curves it supports
             * and the point formats it can parse. Clients SHOULD send both the Supported Elliptic
             * Curves Extension and the Supported Point Formats Extension.
             */
            this.namedCurves = new int[] { NamedCurve.secp256r1, NamedCurve.secp224r1,
                NamedCurve.secp192r1 };
            this.ecPointFormats = new short[] { ECPointFormat.ansiX962_compressed_char2,
                ECPointFormat.ansiX962_compressed_prime, ECPointFormat.uncompressed };

            if (clientExtensions == null) {
                clientExtensions = new Hashtable();
            }

            TlsECCUtils.addSupportedEllipticCurvesExtension(clientExtensions, namedCurves);
            TlsECCUtils.addSupportedPointFormatsExtension(clientExtensions, ecPointFormats);
            return clientExtensions;
        }

        return clientExtensions;
    }

    public TlsKeyExchange getKeyExchange() throws IOException {

        switch (selectedCipherSuite) {
        case CipherSuite.TLS_RSA_WITH_3DES_EDE_CBC_SHA:
        case CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA:
        case CipherSuite.TLS_RSA_WITH_AES_256_CBC_SHA:
        case CipherSuite.TLS_RSA_WITH_CAMELLIA_128_CBC_SHA:
        case CipherSuite.TLS_RSA_WITH_CAMELLIA_256_CBC_SHA:
        case CipherSuite.TLS_RSA_WITH_RC4_128_SHA:
        case CipherSuite.TLS_RSA_WITH_SEED_CBC_SHA:
            return createRSAKeyExchange();

        case CipherSuite.TLS_DH_DSS_WITH_3DES_EDE_CBC_SHA:
        case CipherSuite.TLS_DH_DSS_WITH_AES_128_CBC_SHA:
        case CipherSuite.TLS_DH_DSS_WITH_AES_256_CBC_SHA:
        case CipherSuite.TLS_DH_DSS_WITH_CAMELLIA_128_CBC_SHA:
        case CipherSuite.TLS_DH_DSS_WITH_CAMELLIA_256_CBC_SHA:
        case CipherSuite.TLS_DH_DSS_WITH_SEED_CBC_SHA:
            return createDHKeyExchange(KeyExchangeAlgorithm.DH_DSS);

        case CipherSuite.TLS_DH_RSA_WITH_3DES_EDE_CBC_SHA:
        case CipherSuite.TLS_DH_RSA_WITH_AES_128_CBC_SHA:
        case CipherSuite.TLS_DH_RSA_WITH_AES_256_CBC_SHA:
        case CipherSuite.TLS_DH_RSA_WITH_CAMELLIA_128_CBC_SHA:
        case CipherSuite.TLS_DH_RSA_WITH_CAMELLIA_256_CBC_SHA:
        case CipherSuite.TLS_DH_RSA_WITH_SEED_CBC_SHA:
            return createDHKeyExchange(KeyExchangeAlgorithm.DH_RSA);

        case CipherSuite.TLS_DHE_DSS_WITH_3DES_EDE_CBC_SHA:
        case CipherSuite.TLS_DHE_DSS_WITH_AES_128_CBC_SHA:
        case CipherSuite.TLS_DHE_DSS_WITH_AES_256_CBC_SHA:
        case CipherSuite.TLS_DHE_DSS_WITH_CAMELLIA_128_CBC_SHA:
        case CipherSuite.TLS_DHE_DSS_WITH_CAMELLIA_256_CBC_SHA:
        case CipherSuite.TLS_DHE_DSS_WITH_SEED_CBC_SHA:
            return createDHEKeyExchange(KeyExchangeAlgorithm.DHE_DSS);

        case CipherSuite.TLS_DHE_RSA_WITH_3DES_EDE_CBC_SHA:
        case CipherSuite.TLS_DHE_RSA_WITH_AES_128_CBC_SHA:
        case CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA:
        case CipherSuite.TLS_DHE_RSA_WITH_CAMELLIA_128_CBC_SHA:
        case CipherSuite.TLS_DHE_RSA_WITH_CAMELLIA_256_CBC_SHA:
        case CipherSuite.TLS_DHE_RSA_WITH_SEED_CBC_SHA:
            return createDHEKeyExchange(KeyExchangeAlgorithm.DHE_RSA);

        case CipherSuite.TLS_ECDH_ECDSA_WITH_3DES_EDE_CBC_SHA:
        case CipherSuite.TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA:
        case CipherSuite.TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA:
        case CipherSuite.TLS_ECDH_ECDSA_WITH_RC4_128_SHA:
            return createECDHKeyExchange(KeyExchangeAlgorithm.ECDH_ECDSA);

        case CipherSuite.TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA:
        case CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA:
        case CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA:
        case CipherSuite.TLS_ECDHE_ECDSA_WITH_RC4_128_SHA:
            return createECDHEKeyExchange(KeyExchangeAlgorithm.ECDHE_ECDSA);

        case CipherSuite.TLS_ECDH_RSA_WITH_3DES_EDE_CBC_SHA:
        case CipherSuite.TLS_ECDH_RSA_WITH_AES_128_CBC_SHA:
        case CipherSuite.TLS_ECDH_RSA_WITH_AES_256_CBC_SHA:
        case CipherSuite.TLS_ECDH_RSA_WITH_RC4_128_SHA:
            return createECDHKeyExchange(KeyExchangeAlgorithm.ECDH_RSA);

        case CipherSuite.TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA:
        case CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA:
        case CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA:
        case CipherSuite.TLS_ECDHE_RSA_WITH_RC4_128_SHA:
            return createECDHEKeyExchange(KeyExchangeAlgorithm.ECDHE_RSA);

        default:
            /*
             * Note: internal error here; the TlsProtocol implementation verifies that the
             * server-selected cipher suite was in the list of client-offered cipher suites, so if
             * we now can't produce an implementation, we shouldn't have offered it!
             */
            throw new TlsFatalAlert(AlertDescription.internal_error);
        }
    }

    public TlsCipher getCipher() throws IOException {

        switch (selectedCipherSuite) {
        case CipherSuite.TLS_RSA_WITH_3DES_EDE_CBC_SHA:
        case CipherSuite.TLS_DH_DSS_WITH_3DES_EDE_CBC_SHA:
        case CipherSuite.TLS_DH_RSA_WITH_3DES_EDE_CBC_SHA:
        case CipherSuite.TLS_DHE_DSS_WITH_3DES_EDE_CBC_SHA:
        case CipherSuite.TLS_DHE_RSA_WITH_3DES_EDE_CBC_SHA:
        case CipherSuite.TLS_ECDH_ECDSA_WITH_3DES_EDE_CBC_SHA:
        case CipherSuite.TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA:
        case CipherSuite.TLS_ECDH_RSA_WITH_3DES_EDE_CBC_SHA:
        case CipherSuite.TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA:
            return cipherFactory.createCipher(context, EncryptionAlgorithm._3DES_EDE_CBC,
                DigestAlgorithm.SHA);

        case CipherSuite.TLS_RSA_WITH_RC4_128_SHA:
        case CipherSuite.TLS_ECDH_ECDSA_WITH_RC4_128_SHA:
        case CipherSuite.TLS_ECDHE_ECDSA_WITH_RC4_128_SHA:
        case CipherSuite.TLS_ECDH_RSA_WITH_RC4_128_SHA:
        case CipherSuite.TLS_ECDHE_RSA_WITH_RC4_128_SHA:
            return cipherFactory.createCipher(context, EncryptionAlgorithm.RC4_128,
                DigestAlgorithm.SHA);

        case CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA:
        case CipherSuite.TLS_DH_DSS_WITH_AES_128_CBC_SHA:
        case CipherSuite.TLS_DH_RSA_WITH_AES_128_CBC_SHA:
        case CipherSuite.TLS_DHE_DSS_WITH_AES_128_CBC_SHA:
        case CipherSuite.TLS_DHE_RSA_WITH_AES_128_CBC_SHA:
        case CipherSuite.TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA:
        case CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA:
        case CipherSuite.TLS_ECDH_RSA_WITH_AES_128_CBC_SHA:
        case CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA:
            return cipherFactory.createCipher(context, EncryptionAlgorithm.AES_128_CBC,
                DigestAlgorithm.SHA);

        case CipherSuite.TLS_RSA_WITH_AES_256_CBC_SHA:
        case CipherSuite.TLS_DH_DSS_WITH_AES_256_CBC_SHA:
        case CipherSuite.TLS_DH_RSA_WITH_AES_256_CBC_SHA:
        case CipherSuite.TLS_DHE_DSS_WITH_AES_256_CBC_SHA:
        case CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA:
        case CipherSuite.TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA:
        case CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA:
        case CipherSuite.TLS_ECDH_RSA_WITH_AES_256_CBC_SHA:
        case CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA:
            return cipherFactory.createCipher(context, EncryptionAlgorithm.AES_256_CBC,
                DigestAlgorithm.SHA);

        case CipherSuite.TLS_RSA_WITH_CAMELLIA_128_CBC_SHA:
        case CipherSuite.TLS_DH_DSS_WITH_CAMELLIA_128_CBC_SHA:
        case CipherSuite.TLS_DH_RSA_WITH_CAMELLIA_128_CBC_SHA:
        case CipherSuite.TLS_DHE_DSS_WITH_CAMELLIA_128_CBC_SHA:
        case CipherSuite.TLS_DHE_RSA_WITH_CAMELLIA_128_CBC_SHA:
            return cipherFactory.createCipher(context, EncryptionAlgorithm.CAMELLIA_128_CBC,
                DigestAlgorithm.SHA);

        case CipherSuite.TLS_RSA_WITH_CAMELLIA_256_CBC_SHA:
        case CipherSuite.TLS_DH_DSS_WITH_CAMELLIA_256_CBC_SHA:
        case CipherSuite.TLS_DH_RSA_WITH_CAMELLIA_256_CBC_SHA:
        case CipherSuite.TLS_DHE_DSS_WITH_CAMELLIA_256_CBC_SHA:
        case CipherSuite.TLS_DHE_RSA_WITH_CAMELLIA_256_CBC_SHA:
            return cipherFactory.createCipher(context, EncryptionAlgorithm.CAMELLIA_256_CBC,
                DigestAlgorithm.SHA);

        case CipherSuite.TLS_RSA_WITH_SEED_CBC_SHA:
        case CipherSuite.TLS_DH_DSS_WITH_SEED_CBC_SHA:
        case CipherSuite.TLS_DH_RSA_WITH_SEED_CBC_SHA:
        case CipherSuite.TLS_DHE_DSS_WITH_SEED_CBC_SHA:
        case CipherSuite.TLS_DHE_RSA_WITH_SEED_CBC_SHA:
            return cipherFactory.createCipher(context, EncryptionAlgorithm.SEED_CBC,
                DigestAlgorithm.SHA);

        default:
            /*
             * Note: internal error here; the TlsProtocol implementation verifies that the
             * server-selected cipher suite was in the list of client-offered cipher suites, so if
             * we now can't produce an implementation, we shouldn't have offered it!
             */
            throw new TlsFatalAlert(AlertDescription.internal_error);
        }
    }

    protected TlsKeyExchange createDHKeyExchange(int keyExchange) {
        return new TlsDHKeyExchange(keyExchange);
    }

    protected TlsKeyExchange createDHEKeyExchange(int keyExchange) {
        return new TlsDHEKeyExchange(keyExchange);
    }

    protected TlsKeyExchange createECDHKeyExchange(int keyExchange) {
        return new TlsECDHKeyExchange(keyExchange, namedCurves, ecPointFormats);
    }

    protected TlsKeyExchange createECDHEKeyExchange(int keyExchange) {
        return new TlsECDHEKeyExchange(keyExchange, namedCurves, ecPointFormats);
    }

    protected TlsKeyExchange createRSAKeyExchange() {
        return new TlsRSAKeyExchange();
    }
}
