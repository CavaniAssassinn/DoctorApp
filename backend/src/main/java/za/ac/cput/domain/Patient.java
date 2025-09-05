package za.ac.cput.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class Patient {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable=false) private String fullName;
    @Column(nullable=false, unique=true) private String email;
    private String phone;

    protected Patient() {}
    public Patient(String fullName, String email, String phone){
        this.fullName = fullName; this.email = email; this.phone = phone;
    }
    public UUID getId(){ return id; }
    public String getFullName(){ return fullName; }
    public String getEmail(){ return email; }
    public String getPhone(){ return phone; }
    public void setFullName(String v){ this.fullName=v; }
    public void setEmail(String v){ this.email=v; }
    public void setPhone(String v){ this.phone=v; }
}
