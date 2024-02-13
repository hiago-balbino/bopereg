package br.com.wes.vo.v1.security;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AccountCredentialsVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 2924250126163321817L;

    private String username;
    private String password;

}
