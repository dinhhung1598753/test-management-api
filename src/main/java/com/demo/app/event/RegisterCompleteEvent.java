package com.demo.app.event;

import com.demo.app.model.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class RegisterCompleteEvent extends ApplicationEvent {

    private User user;

    private String url;

    public RegisterCompleteEvent(User user, String url){
        super(user);
        this.user = user;
        this.url = url;
    }
}
