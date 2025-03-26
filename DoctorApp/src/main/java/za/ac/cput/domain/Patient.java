package main.java.za.ac.cput.domain;
import java.util.List;

public class Patient extends User {
    private final String dateOfBirth;
    private final String medicalHistory;
    private final List<Integer> appointmentsIDs;

    private Patient(Builder builder){
        super(builder);
        this.dateOfBirth = builder.dateOfBirth;
        this.medicalHistory = builder.medicalHistory;
        this.appointmentsIDs = buiilder.appointmentsIDs;
    }

    //inner builder class
    public static class Builder extends User.Builder<Builder>{
        private String dateOfBirth;
        private String medicalHistory;
        private List<Integer> appointmentIDs;


        public Builder setDateOfBirth(String dateOfBirth){
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public Builder setMedicalHistory(String medicalHistory){
            this.medicalHistory = medicalHistory;
            return this;
        }

        public Builder setAppointmentIDs(List<Integer>appointmentIDs){
            this.appointmentIDs = appointmentIDs
                    return this;
        }

        @Override
        protected Builder self(){
            return this;
        }

        @Override
        public Patient build(){
            return new Patient(this);
        }

    }

    @Override
    public String toString() {
        return "Patient{" +
                "userID=" + userID +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", role='" + role + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", medicalHistory='" + medicalHistory + '\'' +
                ", appointmentIDs=" + appointmentIDs +
                '}';
    }


}
