package com.tl.hotelproject.utils;

import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.regex.Pattern;

@Service()
public class StringUtils {
    public static String slugify(String input) {
        // Bước 1: Loại bỏ dấu và chuyển thành chữ thường
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String withoutDiacritics = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();

        // Bước 2: Loại bỏ các ký tự không phải chữ cái hoặc số, thay thế bằng dấu gạch ngang
        String slug = withoutDiacritics.replaceAll("[-+^]*", "").replaceAll("\\s+", "-");

        return slug;
    }
}
