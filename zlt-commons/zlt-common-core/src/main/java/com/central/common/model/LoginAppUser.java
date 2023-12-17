package com.central.common.model;

import java.io.Serial;
import java.util.*;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zlt
 * 用户实体绑定spring security
 */
@Getter
@Setter
public class LoginAppUser extends User implements OAuth2AuthenticatedPrincipal {
    @Serial
    private static final long serialVersionUID = -3685249101751401211L;

    @JsonSerialize(using = ToStringSerializer.class)
    private final Long id;

    private final String mobile;

    private final Collection<String> permissions;

    private final Map<String, Object> attributes = new HashMap<>();

    public LoginAppUser() {
        super(" ", "", true, true, true, true, new HashSet<>());
        this.id = null;
        this.mobile = null;
        this.permissions = new HashSet<>(0);
    }

    public LoginAppUser(Long id, String username, Collection<? extends GrantedAuthority> authorities) {
        super(username, ""
                , true, true, true, true, authorities);
        this.id = id;
        this.mobile = null;
        this.permissions = new HashSet<>(0);
    }

    public LoginAppUser(Long id, String username, String password, String mobile, Collection<String> permissions, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, Optional.ofNullable(password).orElse("")
                , enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.id = id;
        this.mobile = mobile;
        this.permissions = Optional.ofNullable(permissions).orElse(new HashSet<>(0));
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public String getName() {
        return this.getUsername();
    }
}
