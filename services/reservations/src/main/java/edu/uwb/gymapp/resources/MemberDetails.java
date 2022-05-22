package edu.uwb.gymapp.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uwb.gymapp.models.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class MemberDetails implements UserDetails {


    private String username;


    private String password;

    public MemberDetails(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public MemberDetails() {}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // we hardcode the roles for now
        String[] strRoles = {"MEMBER"};
        List<GrantedAuthority> authorities = Arrays.stream(strRoles).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @JsonProperty("username")
    public void setUsername(String username) {
        this.username = username;
    }

    @JsonProperty("password")
    public void setPassword(String password) {
        this.password = password;
    }
}
