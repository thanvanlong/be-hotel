package com.tl.hotelproject.service.mail;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;

@Service
@AllArgsConstructor
public class EmailSender implements EmailService {
    private final JavaMailSender mailSender;

    public static void main(String[] args) {
        String userDirectory = Paths.get("")
                .toAbsolutePath()
                .toString();

        String path = userDirectory + "/src/main/java/com/tl/hotelproject/service/mail/template";
        System.out.println(path);
    }

    @Override
    public String readFile(String filename) throws Exception{
        String userDirectory = Paths.get("")
                .toAbsolutePath()
                .toString();

        String path = userDirectory + "/src/main/java/com/tl/hotelproject/service/mail/template/" + filename;
        StringBuilder fileContent = new StringBuilder();
        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                fileContent.append(line).append("\n");
            }

            bufferedReader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileContent.toString();
    }

    @Override
    public void send(String to, String content, String subject, Map<String, Object> body, String filename) {
        if(Objects.equals(filename, "")) {
            filename = "base.html";
        }
        String html = "";

        if(!Objects.equals(content, "")) html = "<div>" +content+ "</div>";
//                "<div style=\"font-family: Helvetica,Arial,sans-serif;min-width:1000px;overflow:auto;line-height:2\">\n" +
//                        "        <div style=\"margin:50px auto;width:70%;padding:20px 0\">\n" +
//                        "          <div style=\"border-bottom:1px solid #eee\">\n" +
//                        "            <a href=\"\" style=\"font-size:1.4em;color: #00466a;text-decoration:none;font-weight:600\">Team 69</a>\n" +
//                        "          </div>\n" +
//                        "          <p style=\"font-size:1.1em\">Hi,</p>\n" +
//                        "          <p>Thank you for choosing out store. Use the following password to login. Please change password and update information after logging</p>\n" +
//                        "          <h2 style=\"background: #00466a;margin: 0 auto;width: max-content;padding: 0 10px;color: #fff;border-radius: 4px;\">"+ content + "</h2>\n" +
//                        "          <p style=\"font-size:0.9em;\">Regards,<br />Team 69</p>\n" +
//                        "          <hr style=\"border:none;border-top:1px solid #eee\" />\n" +
//                        "          <div style=\"float:right;padding:8px 0;color:#aaa;font-size:0.8em;line-height:1;font-weight:300\">\n" +
//                        "            <p>Team 69 Inc</p>\n" +
//                        "            <p>Tran Duy Hung, Hanoi, Vietnam</p>\n" +
//                        "            <p>Vietnam</p>\n" +
//                        "          </div>\n" +
//                        "        </div>\n" +
//                        "      </div>";

        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper =
                    new MimeMessageHelper(message,"utf-8");
            mimeMessageHelper.setText(Objects.equals(html, "")? initContent(body,filename) : html,true);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setFrom("linhntt5922@gmail.com");
            mailSender.send(message);
        }catch(MessagingException e){
            throw new IllegalStateException("Fail to send email");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String initContent(Map<String, Object> body, String filename) {
        try {

            String userDirectory = Paths.get("")
                    .toAbsolutePath()
                    .toString();

            String path = userDirectory + "/src/main/java/com/tl/hotelproject/service/mail/template";
            System.out.println(path);
            Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
            configuration.setDirectoryForTemplateLoading(new File(path));

            Template template = configuration.getTemplate(filename);

            StringWriter writer = new StringWriter();

            template.process(body, writer);

            return writer.toString();
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
        return null;
    }
}
