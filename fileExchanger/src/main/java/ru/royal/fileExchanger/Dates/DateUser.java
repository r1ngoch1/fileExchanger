package ru.royal.fileExchanger;
import ru.royal.fileExchanger.entities.User;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import java.util.List;
import org.springframework.context.annotation.Bean;


@Component
public class DateUser {
    @Bean
    @Scope(value = BeanDefinition.SCOPE_SINGLETON)
    public List<User> userSource(){
        return new ArrayList<>();
    }
}
