package test;

import cn.hutool.core.date.StopWatch;
import cn.hutool.json.JSONUtil;
import com.example.script.command.api.Api;
import com.example.script.command.domain.DataSourceParam;
import com.example.script.command.domain.Param;
import com.example.script.utils.FileUtils;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.Signature;
import java.security.cert.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.example.script.constant.DBConstant.*;

/**
 * @author albert lewis
 * @date 2024/1/22
 */
public class ApiTest {

    private static final Api api=new Api();

    public static void main(String[] args) {
        StopWatch stopWatch=new StopWatch();
        stopWatch.start("任务");
//        Param param=new Param();
//        initSQl(param);
//        api.execute(JSONUtil.toJsonStr(param));
//        param=new Param();
//        diffSQl(param);
//        api.execute(JSONUtil.toJsonStr(param));
//        param=new Param();
//        updateDB(param);
//        api.execute(JSONUtil.toJsonStr(param));
//        stopWatch.stop();

        isKeyValid("29VRVXKXEQ-eyJsaWNlbnNlSWQiOiIyOVZSVlhLWEVRIiwibGljZW5zZWVOYW1lIjoiZ3VyZ2xlcyB0dW1ibGVzIiwiYXNzaWduZWVOYW1lIjoiIiwiYXNzaWduZWVFbWFpbCI6IiIsImxpY2Vuc2VSZXN0cmljdGlvbiI6IiIsImNoZWNrQ29uY3VycmVudFVzZSI6ZmFsc2UsInByb2R1Y3RzIjpbeyJjb2RlIjoiSUkiLCJmYWxsYmFja0RhdGUiOiIyMDI2LTA5LTE0IiwicGFpZFVwVG8iOiIyMDI2LTA5LTE0IiwiZXh0ZW5kZWQiOmZhbHNlfSx7ImNvZGUiOiJQQ1dNUCIsImZhbGxiYWNrRGF0ZSI6IjIwMjYtMDktMTQiLCJwYWlkVXBUbyI6IjIwMjYtMDktMTQiLCJleHRlbmRlZCI6dHJ1ZX0seyJjb2RlIjoiUFNJIiwiZmFsbGJhY2tEYXRlIjoiMjAyNi0wOS0xNCIsInBhaWRVcFRvIjoiMjAyNi0wOS0xNCIsImV4dGVuZGVkIjp0cnVlfSx7ImNvZGUiOiJQREIiLCJmYWxsYmFja0RhdGUiOiIyMDI2LTA5LTE0IiwicGFpZFVwVG8iOiIyMDI2LTA5LTE0IiwiZXh0ZW5kZWQiOnRydWV9XSwibWV0YWRhdGEiOiIwMTIwMjMwOTE0UFNBWDAwMDAwNSIsImhhc2giOiJUUklBTDoxNjQ5MDU4NzE5IiwiZ3JhY2VQZXJpb2REYXlzIjo3LCJhdXRvUHJvbG9uZ2F0ZWQiOmZhbHNlLCJpc0F1dG9Qcm9sb25nYXRlZCI6ZmFsc2V9-YKRuMTrLQcfyWisYF1q6RhCN+Ub13VOCayGGc6tklGA97oxRM1HCIR0oI5yfTjL7UQYDbNMokT0U0ZQ2obYaUx+MMf7+3FfUYp5dYzP7G9YrEehrGWQ4O8ENrDLDAClB8o8jud9cafW9WTx9hDNd9j2FfjwSaRibClwGBRdO5fSkWlKGhx4tV0K9IyotNYDQzT1QCDRWSxHYGqfDAQI2k+ZAqzNEHValupSM3TKw813kFGKIQndMfw57B6uMzgN6PvuuLpBlghdO3imrgKYj0Q59JYbuXRUpHhPnNLY1XmewdlfcJkvTiRwueCPMNEW/CQEh8X/Als92WCr2H3uFRA==-MIIETDCCAjSgAwIBAgIBDTANBgkqhkiG9w0BAQsFADAYMRYwFAYDVQQDDA1KZXRQcm9maWxlIENBMB4XDTIwMTAxOTA5MDU1M1oXDTIyMTAyMTA5MDU1M1owHzEdMBsGA1UEAwwUcHJvZDJ5LWZyb20tMjAyMDEwMTkwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCUlaUFc1wf+CfY9wzFWEL2euKQ5nswqb57V8QZG7d7RoR6rwYUIXseTOAFq210oMEe++LCjzKDuqwDfsyhgDNTgZBPAaC4vUU2oy+XR+Fq8nBixWIsH668HeOnRK6RRhsr0rJzRB95aZ3EAPzBuQ2qPaNGm17pAX0Rd6MPRgjp75IWwI9eA6aMEdPQEVN7uyOtM5zSsjoj79Lbu1fjShOnQZuJcsV8tqnayeFkNzv2LTOlofU/Tbx502Ro073gGjoeRzNvrynAP03pL486P3KCAyiNPhDs2z8/COMrxRlZW5mfzo0xsK0dQGNH3UoG/9RVwHG4eS8LFpMTR9oetHZBAgMBAAGjgZkwgZYwCQYDVR0TBAIwADAdBgNVHQ4EFgQUJNoRIpb1hUHAk0foMSNM9MCEAv8wSAYDVR0jBEEwP4AUo562SGdCEjZBvW3gubSgUouX8bOhHKQaMBgxFjAUBgNVBAMMDUpldFByb2ZpbGUgQ0GCCQDSbLGDsoN54TATBgNVHSUEDDAKBggrBgEFBQcDATALBgNVHQ8EBAMCBaAwDQYJKoZIhvcNAQELBQADggIBABKaDfYJk51mtYwUFK8xqhiZaYPd30TlmCmSAaGJ0eBpvkVeqA2jGYhAQRqFiAlFC63JKvWvRZO1iRuWCEfUMkdqQ9VQPXziE/BlsOIgrL6RlJfuFcEZ8TK3syIfIGQZNCxYhLLUuet2HE6LJYPQ5c0jH4kDooRpcVZ4rBxNwddpctUO2te9UU5/FjhioZQsPvd92qOTsV+8Cyl2fvNhNKD1Uu9ff5AkVIQn4JU23ozdB/R5oUlebwaTE6WZNBs+TA/qPj+5/we9NH71WRB0hqUoLI2AKKyiPw++FtN4Su1vsdDlrAzDj9ILjpjJKA1ImuVcG329/WTYIKysZ1CWK3zATg9BeCUPAV1pQy8ToXOq+RSYen6winZ2OO93eyHv2Iw5kbn1dqfBw1BuTE29V2FJKicJSu8iEOpfoafwJISXmz1wnnWL3V/0NxTulfWsXugOoLfv0ZIBP1xH9kmf22jjQ2JiHhQZP7ZDsreRrOeIQ/c4yR8IQvMLfC0WKQqrHu5ZzXTH4NO3CwGWSlTY74kE91zXB5mwWAx1jig+UXYc2w4RkVhy0//lOmVya/PEepuuTTI4+UJwC7qbVlh5zfhj8oTNUXgN0AOc+Q0/WFPl1aw5VV/VrO8FCoB15lFVlpKaQ1Yh+DVU8ke+rt9Th0BCHXe0uZOEmH0nOnH/0onD");
        System.out.println(stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
//        test();
    }
    private static final String[] ROOT_CERTIFICATES = new String[]{
            "-----BEGIN CERTIFICATE-----\n" +
                    "MIIFOzCCAyOgAwIBAgIJANJssYOyg3nhMA0GCSqGSIb3DQEBCwUAMBgxFjAUBgNV\n" +
                    "BAMMDUpldFByb2ZpbGUgQ0EwHhcNMTUxMDAyMTEwMDU2WhcNNDUxMDI0MTEwMDU2\n" +
                    "WjAYMRYwFAYDVQQDDA1KZXRQcm9maWxlIENBMIICIjANBgkqhkiG9w0BAQEFAAOC\n" +
                    "Ag8AMIICCgKCAgEA0tQuEA8784NabB1+T2XBhpB+2P1qjewHiSajAV8dfIeWJOYG\n" +
                    "y+ShXiuedj8rL8VCdU+yH7Ux/6IvTcT3nwM/E/3rjJIgLnbZNerFm15Eez+XpWBl\n" +
                    "m5fDBJhEGhPc89Y31GpTzW0vCLmhJ44XwvYPntWxYISUrqeR3zoUQrCEp1C6mXNX\n" +
                    "EpqIGIVbJ6JVa/YI+pwbfuP51o0ZtF2rzvgfPzKtkpYQ7m7KgA8g8ktRXyNrz8bo\n" +
                    "iwg7RRPeqs4uL/RK8d2KLpgLqcAB9WDpcEQzPWegbDrFO1F3z4UVNH6hrMfOLGVA\n" +
                    "xoiQhNFhZj6RumBXlPS0rmCOCkUkWrDr3l6Z3spUVgoeea+QdX682j6t7JnakaOw\n" +
                    "jzwY777SrZoi9mFFpLVhfb4haq4IWyKSHR3/0BlWXgcgI6w6LXm+V+ZgLVDON52F\n" +
                    "LcxnfftaBJz2yclEwBohq38rYEpb+28+JBvHJYqcZRaldHYLjjmb8XXvf2MyFeXr\n" +
                    "SopYkdzCvzmiEJAewrEbPUaTllogUQmnv7Rv9sZ9jfdJ/cEn8e7GSGjHIbnjV2ZM\n" +
                    "Q9vTpWjvsT/cqatbxzdBo/iEg5i9yohOC9aBfpIHPXFw+fEj7VLvktxZY6qThYXR\n" +
                    "Rus1WErPgxDzVpNp+4gXovAYOxsZak5oTV74ynv1aQ93HSndGkKUE/qA/JECAwEA\n" +
                    "AaOBhzCBhDAdBgNVHQ4EFgQUo562SGdCEjZBvW3gubSgUouX8bMwSAYDVR0jBEEw\n" +
                    "P4AUo562SGdCEjZBvW3gubSgUouX8bOhHKQaMBgxFjAUBgNVBAMMDUpldFByb2Zp\n" +
                    "bGUgQ0GCCQDSbLGDsoN54TAMBgNVHRMEBTADAQH/MAsGA1UdDwQEAwIBBjANBgkq\n" +
                    "hkiG9w0BAQsFAAOCAgEAjrPAZ4xC7sNiSSqh69s3KJD3Ti4etaxcrSnD7r9rJYpK\n" +
                    "BMviCKZRKFbLv+iaF5JK5QWuWdlgA37ol7mLeoF7aIA9b60Ag2OpgRICRG79QY7o\n" +
                    "uLviF/yRMqm6yno7NYkGLd61e5Huu+BfT459MWG9RVkG/DY0sGfkyTHJS5xrjBV6\n" +
                    "hjLG0lf3orwqOlqSNRmhvn9sMzwAP3ILLM5VJC5jNF1zAk0jrqKz64vuA8PLJZlL\n" +
                    "S9TZJIYwdesCGfnN2AETvzf3qxLcGTF038zKOHUMnjZuFW1ba/12fDK5GJ4i5y+n\n" +
                    "fDWVZVUDYOPUixEZ1cwzmf9Tx3hR8tRjMWQmHixcNC8XEkVfztID5XeHtDeQ+uPk\n" +
                    "X+jTDXbRb+77BP6n41briXhm57AwUI3TqqJFvoiFyx5JvVWG3ZqlVaeU/U9e0gxn\n" +
                    "8qyR+ZA3BGbtUSDDs8LDnE67URzK+L+q0F2BC758lSPNB2qsJeQ63bYyzf0du3wB\n" +
                    "/gb2+xJijAvscU3KgNpkxfGklvJD/oDUIqZQAnNcHe7QEf8iG2WqaMJIyXZlW3me\n" +
                    "0rn+cgvxHPt6N4EBh5GgNZR4l0eaFEV+fxVsydOQYo1RIyFMXtafFBqQl6DDxujl\n" +
                    "FeU3FZ+Bcp12t7dlM4E0/sS1XdL47CfGVj4Bp+/VbF862HmkAbd7shs7sDQkHbU=\n" +
                    "-----END CERTIFICATE-----\n",
            "-----BEGIN CERTIFICATE-----\n" +
                    "MIIFTDCCAzSgAwIBAgIJAMCrW9HV+hjZMA0GCSqGSIb3DQEBCwUAMB0xGzAZBgNV\n" +
                    "BAMMEkxpY2Vuc2UgU2VydmVycyBDQTAgFw0xNjEwMTIxNDMwNTRaGA8yMTE2MTIy\n" +
                    "NzE0MzA1NFowHTEbMBkGA1UEAwwSTGljZW5zZSBTZXJ2ZXJzIENBMIICIjANBgkq\n" +
                    "hkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAoT7LvHj3JKK2pgc5f02z+xEiJDcvlBi6\n" +
                    "fIwrg/504UaMx3xWXAE5CEPelFty+QPRJnTNnSxqKQQmg2s/5tMJpL9lzGwXaV7a\n" +
                    "rrcsEDbzV4el5mIXUnk77Bm/QVv48s63iQqUjVmvjQt9SWG2J7+h6X3ICRvF1sQB\n" +
                    "yeat/cO7tkpz1aXXbvbAws7/3dXLTgAZTAmBXWNEZHVUTcwSg2IziYxL8HRFOH0+\n" +
                    "GMBhHqa0ySmF1UTnTV4atIXrvjpABsoUvGxw+qOO2qnwe6ENEFWFz1a7pryVOHXg\n" +
                    "P+4JyPkI1hdAhAqT2kOKbTHvlXDMUaxAPlriOVw+vaIjIVlNHpBGhqTj1aqfJpLj\n" +
                    "qfDFcuqQSI4O1W5tVPRNFrjr74nDwLDZnOF+oSy4E1/WhL85FfP3IeQAIHdswNMJ\n" +
                    "y+RdkPZCfXzSUhBKRtiM+yjpIn5RBY+8z+9yeGocoxPf7l0or3YF4GUpud202zgy\n" +
                    "Y3sJqEsZksB750M0hx+vMMC9GD5nkzm9BykJS25hZOSsRNhX9InPWYYIi6mFm8QA\n" +
                    "2Dnv8wxAwt2tDNgqa0v/N8OxHglPcK/VO9kXrUBtwCIfZigO//N3hqzfRNbTv/ZO\n" +
                    "k9lArqGtcu1hSa78U4fuu7lIHi+u5rgXbB6HMVT3g5GQ1L9xxT1xad76k2EGEi3F\n" +
                    "9B+tSrvru70CAwEAAaOBjDCBiTAdBgNVHQ4EFgQUpsRiEz+uvh6TsQqurtwXMd4J\n" +
                    "8VEwTQYDVR0jBEYwRIAUpsRiEz+uvh6TsQqurtwXMd4J8VGhIaQfMB0xGzAZBgNV\n" +
                    "BAMMEkxpY2Vuc2UgU2VydmVycyBDQYIJAMCrW9HV+hjZMAwGA1UdEwQFMAMBAf8w\n" +
                    "CwYDVR0PBAQDAgEGMA0GCSqGSIb3DQEBCwUAA4ICAQCJ9+GQWvBS3zsgPB+1PCVc\n" +
                    "oG6FY87N6nb3ZgNTHrUMNYdo7FDeol2DSB4wh/6rsP9Z4FqVlpGkckB+QHCvqU+d\n" +
                    "rYPe6QWHIb1kE8ftTnwapj/ZaBtF80NWUfYBER/9c6To5moW63O7q6cmKgaGk6zv\n" +
                    "St2IhwNdTX0Q5cib9ytE4XROeVwPUn6RdU/+AVqSOspSMc1WQxkPVGRF7HPCoGhd\n" +
                    "vqebbYhpahiMWfClEuv1I37gJaRtsoNpx3f/jleoC/vDvXjAznfO497YTf/GgSM2\n" +
                    "LCnVtpPQQ2vQbOfTjaBYO2MpibQlYpbkbjkd5ZcO5U5PGrQpPFrWcylz7eUC3c05\n" +
                    "UVeygGIthsA/0hMCioYz4UjWTgi9NQLbhVkfmVQ5lCVxTotyBzoubh3FBz+wq2Qt\n" +
                    "iElsBrCMR7UwmIu79UYzmLGt3/gBdHxaImrT9SQ8uqzP5eit54LlGbvGekVdAL5l\n" +
                    "DFwPcSB1IKauXZvi1DwFGPeemcSAndy+Uoqw5XGRqE6jBxS7XVI7/4BSMDDRBz1u\n" +
                    "a+JMGZXS8yyYT+7HdsybfsZLvkVmc9zVSDI7/MjVPdk6h0sLn+vuPC1bIi5edoNy\n" +
                    "PdiG2uPH5eDO6INcisyPpLS4yFKliaO4Jjap7yzLU9pbItoWgCAYa2NpxuxHJ0tB\n" +
                    "7tlDFnvaRnQukqSG+VqNWg==\n" +
                    "-----END CERTIFICATE-----"
    };




    private static boolean isEvaluationValid(String expirationTime) {
        try {
            final Date now = new Date();
            final Date expiration = new Date(Long.parseLong(expirationTime));
            return now.before(expiration);
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isKeyValid(String key) {
        String[] licenseParts = key.split("-");
        if (licenseParts.length !=  4) {
            return false; // invalid format
        }

        final String licenseId = licenseParts[0];
        final String licensePartBase64 = licenseParts[1];
        final String signatureBase64 = licenseParts[2];
        final String certBase64 = licenseParts[3];

        try {
            final Signature sig = Signature.getInstance("SHA1withRSA");
            byte[] licenseBytes = Base64.getMimeDecoder().decode(licensePartBase64.getBytes(StandardCharsets.UTF_8));
            String licenseData = new String(licenseBytes, StandardCharsets.UTF_8);
            System.out.println(licenseData);
            // the last parameter of 'createCertificate()' set to 'false' switches off certificate expiration checks.
            // This might be the case if the key is at the same time a perpetual fallback license for older IDE versions.
            // Here it is only important that the key was signed with an authentic JetBrains certificate.
            sig.initVerify(createCertificate(
                    Base64.getMimeDecoder().decode(certBase64.getBytes(StandardCharsets.UTF_8)), Collections.emptySet(), false
            ));
            licenseBytes = Base64.getMimeDecoder().decode(licensePartBase64.getBytes(StandardCharsets.UTF_8));
            sig.update(licenseBytes);
            if (!sig.verify(Base64.getMimeDecoder().decode(signatureBase64.getBytes(StandardCharsets.UTF_8)))) {
                return false;
            }
            // Optional additional check: the licenseId corresponds to the licenseId encoded in the signed license data
            // The following is a 'least-effort' code. It would be more accurate to parse json and then find there the value of the attribute "licenseId"
            licenseData = new String(licenseBytes, StandardCharsets.UTF_8);
            System.out.println(licenseData);
            return licenseData.contains("\"licenseId\":\"" + licenseId + "\"");
        }
        catch (Throwable e) {
            e.printStackTrace(); // For debug purposes only. Normally you will not want to print exception's trace to console
        }
        return false;
    }

    private static X509Certificate createCertificate(byte[] certBytes, Collection<byte[]> intermediateCertsBytes, boolean checkValidityAtCurrentDate) throws Exception {
        final CertificateFactory x509factory = CertificateFactory.getInstance("X.509");
        final X509Certificate cert = (X509Certificate) x509factory.generateCertificate(new ByteArrayInputStream(certBytes));

        final Collection<Certificate> allCerts = new HashSet<>();
        allCerts.add(cert);
        for (byte[] bytes : intermediateCertsBytes) {
            allCerts.add(x509factory.generateCertificate(new ByteArrayInputStream(bytes)));
        }

        try {
            // Create the selector that specifies the starting certificate
            final X509CertSelector selector = new X509CertSelector();
            selector.setCertificate(cert);
            // Configure the PKIX certificate builder algorithm parameters
            final Set<TrustAnchor> trustAchors = new HashSet<>();
            for (String rc : ROOT_CERTIFICATES) {
                trustAchors.add(new TrustAnchor(
                        (X509Certificate) x509factory.generateCertificate(new ByteArrayInputStream(rc.getBytes(StandardCharsets.UTF_8))), null
                ));
            }

            final PKIXBuilderParameters pkixParams = new PKIXBuilderParameters(trustAchors, selector);
            pkixParams.setRevocationEnabled(false);
            if (!checkValidityAtCurrentDate) {
                // deliberately check validity on the start date of cert validity period, so that we do not depend on
                // the actual moment when the check is performed
                pkixParams.setDate(cert.getNotBefore());
            }
            pkixParams.addCertStore(
                    CertStore.getInstance("Collection", new CollectionCertStoreParameters(allCerts))
            );
            // Build and verify the certification chain
            final CertPath path = CertPathBuilder.getInstance("PKIX").build(pkixParams).getCertPath();
            if (path != null) {
                CertPathValidator.getInstance("PKIX").validate(path, pkixParams);
                return cert;
            }
        }
        catch (Exception e) {
            // debug the reason here
        }
        throw new Exception ("Certificate used to sign the license is not signed by JetBrains root certificate");
    }


    private static void test(){
        StopWatch stopWatch=new StopWatch();
        stopWatch.start("任务");
        String[] args={"--file=./param.json"};
        Map<String, String> commandParam = getCommandParam(args);
        if (commandParam.get("file")==null||commandParam.get("file").isBlank()){
            throw new RuntimeException("文件路径不存在");
        }

        String paramFilePath = commandParam.get("file");
        String jsonFile = FileUtils.getJsonFile(paramFilePath);
        if (jsonFile==null||jsonFile.isBlank()||!JSONUtil.isTypeJSON(jsonFile)){
            throw new RuntimeException("文件内容格式不正确");
        }

        Api api=new Api();
        api.execute(jsonFile);
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
    }
    private static Map<String,String> getCommandParam(String[] args){
        Map<String, String> commandLineArgs = new HashMap<>();

        for (String arg : args) {
            System.out.println(arg);
            if (arg.startsWith("--")) {
                String[] parts = arg.substring(2).split("=", 2);
                if (parts.length == 2) {
                    String key = parts[0];
                    String value = parts[1];
                    if ("null".equalsIgnoreCase(value)){
                        value=null;
                    }
                    commandLineArgs.put(key, value);
                }
            }
        }
        return commandLineArgs;
    }
    private static void diffSQl(Param param) {

        param.setType("diff");
        param.setSourceType("ds");
        DataSourceParam sourceDataSourceParam=new DataSourceParam();
        sourceDataSourceParam.setType(param.getSourceType());
        sourceDataSourceParam.setUrl(SOURCE_URL);
        sourceDataSourceParam.setUserName(SOURCE_USERNAME);
        sourceDataSourceParam.setPassword(SOURCE_PASSWORD);
        sourceDataSourceParam.setDriverName(DRIVER_CLASS_NAME);
        param.setSourceDataParam(JSONUtil.toJsonStr(sourceDataSourceParam));

        param.setTargetType("ds");
        DataSourceParam targetDataSourceParam=new DataSourceParam();
        targetDataSourceParam.setType(param.getSourceType());
        targetDataSourceParam.setUrl(TARGET_URL);
        targetDataSourceParam.setUserName(TARGET_USERNAME);
        targetDataSourceParam.setPassword(TARGET_PASSWORD);
        targetDataSourceParam.setDriverName(DRIVER_CLASS_NAME);
        param.setTargetDataParam(JSONUtil.toJsonStr(targetDataSourceParam));

    }

    private static void initSQl(Param param) {
        param.setType("init");
        param.setSourceType("ds");
        DataSourceParam dataSourceParam=new DataSourceParam();
        dataSourceParam.setType(param.getSourceType());
        dataSourceParam.setUrl(SOURCE_URL);
        dataSourceParam.setUserName(SOURCE_USERNAME);
        dataSourceParam.setPassword(SOURCE_PASSWORD);
        dataSourceParam.setDriverName(DRIVER_CLASS_NAME);
        param.setSourceDataParam(JSONUtil.toJsonStr(dataSourceParam));
    }

    private static void updateDB(Param param){
        param.setType("diff");
        DataSourceParam targetDataSourceParam=new DataSourceParam();
        targetDataSourceParam.setType(param.getSourceType());
        targetDataSourceParam.setUrl(TARGET_URL);
        targetDataSourceParam.setUserName(TARGET_USERNAME);
        targetDataSourceParam.setPassword(TARGET_PASSWORD);
        targetDataSourceParam.setDriverName(DRIVER_CLASS_NAME);
        param.setIsExecute(true);
        param.setTargetDataParam(JSONUtil.toJsonStr(targetDataSourceParam));
    }
}
