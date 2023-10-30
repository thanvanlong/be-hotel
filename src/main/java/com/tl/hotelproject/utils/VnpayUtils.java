package com.tl.hotelproject.utils;

import com.tl.hotelproject.dtos.bills.VnpayCreatePaymentDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VnpayUtils {
    @Value("${VNP_TMNCODE}")
    private String VNP_TMNCODE;

    @Value("${VNP_HASH_SECRET}")
    private String VNP_HASH_SECRET;

    @Value("${VNP_URL}")
    private String VNP_URL;

    @Value("${VNP_RETURN_URL}")
    private String VNP_RETURN_URL;

    public VnpayUtils() {
    }

//    public String createPayment(VnpayCreatePaymentDto dataBody) throws Exception{
//        String tmnCode = VNP_TMNCODE.trim();
//        String secretKey = VNP_HASH_SECRET.trim();
//
//        String vnpUrl = VNP_URL;
//        String returnUrl = VNP_RETURN_URL;
//        String orderId = dataBody.getOrderId();
//        double amount = dataBody.getAmount();
//        String bankCode = dataBody.getBankCode();
//        String ipAddr = "%3A%3A1";
//
//        String locale = dataBody.getLanguage() != null ? dataBody.getLanguage() : "vi";
//
//        String currCode = "VND";
//        Map<String, String> vnpParams = new HashMap<>();
//        vnpParams.put("vnp_Version", "2.1.0");
//        vnpParams.put("vnp_Command", "pay");
//        vnpParams.put("vnp_TmnCode", tmnCode);
//        vnpParams.put("vnp_Locale", locale);
//        vnpParams.put("vnp_CurrCode", currCode);
//        vnpParams.put("vnp_TxnRef", orderId);
//        vnpParams.put("vnp_OrderInfo", "Thanh toan cho ma GD:" + orderId);
//        vnpParams.put("vnp_OrderType", "other");
//        vnpParams.put("vnp_Amount", String.valueOf(amount * 100));
//        vnpParams.put("vnp_ReturnUrl", returnUrl);
//        vnpParams.put("vnp_IpAddr", ipAddr);
//
//        // Calendar and Date formatting
//        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
//        String vnp_CreateDate = formatter.format(cld.getTime());
//        vnpParams.put("vnp_CreateDate", vnp_CreateDate);
//        cld.add(Calendar.MINUTE, 15);
//        String vnp_ExpireDate = formatter.format(cld.getTime());
//        vnpParams.put("vnp_ExpireDate", vnp_ExpireDate);
//        vnpParams.put("vnp_CreateDate", dateFormat.format(new Date()));
//
//        if (bankCode != null) {
//            vnpParams.put("vnp_BankCode", bankCode);
//        }
//
//        vnpParams = sortMap(vnpParams);
//        String signData = joinMapToString(vnpParams, "&");
//
//        String signed = signData(signData, secretKey);
//        vnpParams.put("vnp_SecureHash", signed);
//        vnpUrl += "?" + joinMapToString(vnpParams, "&");
//
////        Map<String, String> result = new HashMap<>();
////        result.put("payUrl", vnpUrl);
//        return vnpUrl;
//    }

    public String createPayment(VnpayCreatePaymentDto createPaymentDto) throws Exception{
    String vnp_Version = "2.1.0";
    String vnp_Command = "pay";
    String vnp_TxnRef = createPaymentDto.getOrderId();
    String vnp_IpAddr = "127.0.0.1";
    String vnp_TmnCode = VNP_TMNCODE;

    Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version",vnp_Version);
        vnp_Params.put("vnp_Command",vnp_Command);
        vnp_Params.put("vnp_TmnCode",vnp_TmnCode);
        vnp_Params.put("vnp_Amount",String.valueOf(createPaymentDto.getAmount() * 100));
        vnp_Params.put("vnp_CurrCode","VND");
        if(createPaymentDto.getBankCode() !=null && !createPaymentDto.getBankCode().isEmpty()) {
            vnp_Params.put("vnp_BankCode", createPaymentDto.getBankCode());
        }

        vnp_Params.put("vnp_TxnRef",vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan cho ma GD: " + createPaymentDto.getOrderId());
        vnp_Params.put("vnp_OrderType", "other");

        String locale = createPaymentDto.getLanguage() != null && !createPaymentDto.getLanguage().isEmpty() ? createPaymentDto.getLanguage() : "vn";

        vnp_Params.put("vnp_Locale",locale);
        vnp_Params.put("vnp_ReturnUrl",VNP_RETURN_URL); // Replace with your actual value.
        vnp_Params.put("vnp_IpAddr",vnp_IpAddr);

    // Calendar and Date formatting
    Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
    String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate",vnp_CreateDate);
        cld.add(Calendar.MINUTE,15);
    String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate",vnp_ExpireDate);

    // Billing and Invoice details
//        vnp_Params.put("vnp_Bill_Mobile",txt_billing_mobile);
//        vnp_Params.put("vnp_Bill_Email",txt_billing_email);

//    String fullName = txt_billing_fullname.trim();
//    if(fullName !=null&&!fullName.isEmpty())
//
//    {
//        int idx = fullName.indexOf(' ');
//        String firstName = fullName.substring(0, idx);
//        String lastName = fullName.substring(fullName.lastIndexOf(' ') + 1);
//        vnp_Params.put("vnp_Bill_FirstName", firstName);
//        vnp_Params.put("vnp_Bill_LastName", lastName);
//    }
//
//        vnp_Params.put("vnp_Bill_Address",txt_inv_addr1);
//        vnp_Params.put("vnp_Bill_City",txt_bill_city);
//        vnp_Params.put("vnp_Bill_Country",txt_bill_country);
//        if(txt_bill_state !=null&&!txt_bill_state.isEmpty())
//
//    {
//        vnp_Params.put("vnp_Bill_State", txt_bill_state);
//    }
//
//        vnp_Params.put("vnp_Inv_Phone",txt_inv_mobile);
//        vnp_Params.put("vnp_Inv_Email",txt_inv_email);
//        vnp_Params.put("vnp_Inv_Customer",txt_inv_customer);
//        vnp_Params.put("vnp_Inv_Address",txt_inv_addr1);
//        vnp_Params.put("vnp_Inv_Company",txt_inv_company);
//        vnp_Params.put("vnp_Inv_Taxcode",txt_inv_taxcode);
//        vnp_Params.put("vnp_Inv_Type",cbo_inv_type);

    vnp_Params = sortMap(vnp_Params);

    String signData = joinMapToString(vnp_Params, "&");
    String vnp_SecureHash = hmacSHA512(VNP_HASH_SECRET, signData);
        vnp_Params.put("vnp_SecureHash",vnp_SecureHash);

    String queryUrl = joinMapToString(vnp_Params, "&");
    String paymentUrl = VNP_URL + "?" + queryUrl;

    return paymentUrl;
}

    private Map<String, String> sortMap(Map<String, String> map) {
        TreeMap<String, String> sortedMap = new TreeMap<>(map);
        return sortedMap;
    }


    private String joinMapToString(Map<String, String> map, String delimiter) throws UnsupportedEncodingException {
        List<String> keyValuePairs = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            keyValuePairs.add(key + "=" + URLEncoder.encode(value, StandardCharsets.US_ASCII.toString()));
        }
        return String.join(delimiter, keyValuePairs);
    }


    public String encodeValue(String value) {
        return value.replace(" ", "+");
    }

    public String signData(String data, String secretKey) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] hashBytes = md.digest((data + secretKey).getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xFF & hashByte);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Handle the exception or throw it as needed
            return null;
        }
    }

    public String hmacSHA512(String key, final String data) {
        try {

            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception ex) {
            return "";
        }
    }
    private String getIpAddress() {
        // Implement the logic to get the IP address from the request.
        // You may need to use HttpServletRequest to extract the IP address.
        return "::1";
    }

    private String getRandomNumber(int length) {
        // Implement logic to generate a random number of the specified length.
        // You can use SecureRandom or other methods to generate the number.
        // Return the generated random number.
        return "";
    }



}
//
//

//import java.util.*;
//import java.nio.charset.StandardCharsets;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.text.SimpleDateFormat;
//import java.net.URLEncoder;
//import java.io.UnsupportedEncodingException;
//
//import com.tl.hotelproject.dtos.bills.VnpayCreatePaymentDto;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//@Service
//public class VnpayUtils {
//    @Value("${VNP_URL}")
//    private String vnpUrl;
//
//    @Value("${VNP_RETURN_URL}")
//    private String returnUrl;
//
//    @Value("${VNP_TMNCODE}")
//    private String tmnCode;
//
//    @Value("${VNP_HASH_SECRET}")
//    private String secretKey;
//
//    public String createPayment(VnpayCreatePaymentDto dataBody) throws NoSuchAlgorithmException, UnsupportedEncodingException {
//        String orderId = dataBody.getOrderId();
//        double amount = dataBody.getAmount();
//        String bankCode = dataBody.getBankCode();
//
////        String locale = dataBody.getOrDefault("language", "vi");
//        String locale = dataBody.getLanguage() != null ? dataBody.getLanguage() : "vi";
//
//        Map<String, String> vnpParams = new TreeMap<>();
//        vnpParams.put("vnp_Version", "2.0.0");
//        vnpParams.put("vnp_Command", "pay");
//        vnpParams.put("vnp_TmnCode", tmnCode);
//        vnpParams.put("vnp_Locale", locale);
//        vnpParams.put("vnp_CurrCode", "VND");
//        vnpParams.put("vnp_TxnRef", orderId);
//        vnpParams.put("vnp_OrderInfo", "Thanh toan cho ma GD:" + orderId);
//        vnpParams.put("vnp_OrderType", "other");
//        vnpParams.put("vnp_Amount", String.valueOf(amount));
//        vnpParams.put("vnp_ReturnUrl", returnUrl);
//        vnpParams.put("vnp_IpAddr", "::1");
//        vnpParams.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
//
//        if (bankCode != null && !bankCode.isEmpty()) {
//            vnpParams.put("vnp_BankCode", bankCode);
//        }
//
//        StringBuilder signData = new StringBuilder();
//        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
//            signData.append(entry.getKey()).append('=').append(entry.getValue()).append('&');
//        }
//
//        MessageDigest digest = MessageDigest.getInstance("SHA-256");
//        byte[] hash = digest.digest(signData.toString().getBytes(StandardCharsets.UTF_8));
//
//        StringBuilder hexString = new StringBuilder();
//        for (byte b : hash) {
//            hexString.append(String.format("%02x", b));
//        }
//
//        String queryUrl = vnpUrl + "?" + signData.toString() + "vnp_SecureHashType=SHA256&vnp_SecureHash=" + hexString.toString();
//
//        return queryUrl;
//    }
//}
