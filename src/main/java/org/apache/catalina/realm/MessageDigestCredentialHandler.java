package org.apache.catalina.realm;

/**
 * This credential handler supports the following forms of stored passwords:
 * <ul>
 * <li><b>encodedCredential</b> - a hex encoded digest of the password digested using the configured digest</li>
 * <li><b>{MD5}encodedCredential</b> - a Base64 encoded MD5 digest of the password</li>
 * <li><b>{SHA}encodedCredential</b> - a Base64 encoded SHA1 digest of the password</li>
 * <li><b>{SSHA}encodedCredential</b> - 20 byte Base64 encoded SHA1 digest followed by variable length salt.
 *
 * <pre>
 * {SSHA}&lt;sha-1 digest:20&gt;&lt;salt:n&gt;
 * </pre>
 *
 * </li>
 * <li><b>salt$iterationCount$encodedCredential</b> - a hex encoded salt, iteration code and a hex encoded credential,
 * each separated by $</li>
 * </ul>
 * <p>
 * If the stored password form does not include an iteration count then an iteration count of 1 is used.
 * <p>
 * If the stored password form does not include salt then no salt is used.
 */
public class MessageDigestCredentialHandler extends DigestCredentialHandlerBase {
}
