package online.db.servise;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MailSenderToAdmin {

    JavaMailSender javaMailSender;

    public void sendEmailToAdmin(String reviewBasket, String dataUser) {

        String massage = dataUser + '\n'
                + "Карзина: " + reviewBasket;

        send(massage, "testonlinestoret2@gmail.com");
    }

    public void send(String massage, String email) {

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setFrom("testonlinestoret@gmail.com");
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject("Заказ");
        simpleMailMessage.setText(massage);

        javaMailSender.send(simpleMailMessage);
    }

}
