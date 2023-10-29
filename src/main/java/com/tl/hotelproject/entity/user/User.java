package com.tl.hotelproject.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tl.hotelproject.entity.CommonObjectDTO;
import com.tl.hotelproject.entity.bill.Bill;
import com.tl.hotelproject.entity.booking.Booking;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "userTbl")
public class User extends CommonObjectDTO implements UserDetails {
    @Id
    @UuidGenerator
    private String id;
    @Email(message = "Email is not valid", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    @NotBlank(message = "email is mandatory")
    private String email;
    private String password;
    private String name;
    private String address;
    private String phoneNumber;
    private boolean isActive;
    private Collection<Role> roles = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Booking> bookingList;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Bill> bills;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<SimpleGrantedAuthority> roleList = new ArrayList<>();
        roles.stream().forEach(role -> roleList.add(new SimpleGrantedAuthority(role.name())));
        return roleList;
    }

    public void setRoles(Role role){
        Collection<Role> roles1 = new ArrayList<>();
        roles1.add(role);
        this.roles = roles1;
    }

    public User(String email) {
        this.email = email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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

    public void mapping(User user) {
        this.password = user.getPassword();
        this.name = user.getName();
        this.address = user.getAddress();
        this.phoneNumber = user.getPhoneNumber();
        this.isActive = user.isActive();
        this.roles = user.getRoles();
    }
}
