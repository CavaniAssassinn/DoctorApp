package za.ac.cput.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class Doctor {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable=false) private String fullName;
    @Column(nullable=false) private String speciality;   // <- use THIS name
    @Column(nullable=false) private String clinicCity;

    protected Doctor() {}
    public Doctor(String fullName, String speciality, String clinicCity){
        this.fullName = fullName; this.speciality = speciality; this.clinicCity = clinicCity;
    }
    public UUID getId(){ return id; }
    public String getFullName(){ return fullName; }
    public String getSpeciality(){ return speciality; }
    public String getClinicCity(){ return clinicCity; }
    public void setFullName(String v){ this.fullName=v; }
    public void setSpeciality(String v){ this.speciality=v; }
    public void setClinicCity(String v){ this.clinicCity=v; }
}
