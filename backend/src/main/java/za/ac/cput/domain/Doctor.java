/* Doctor.java
Doctor model class
Author : Matthew Michael Engelbrecht(222381086)
Date : March 2025
 */

package za.ac.cput.domain;

public class Doctor {
    private String doctorID;
    private String specialization;
    private boolean availability;


    private Doctor(DoctorBuilder dbuilder){

        this.doctorID = dbuilder.doctorID;
        this.specialization = dbuilder.specialization;
        this.availability = dbuilder.availability;
    }

    public String getDoctorID(){
        return doctorID;
    }

    public String getSpecialization(){
        return specialization;
    }

    public boolean isAvailable() {
        return availability;
    }


    public void displayDoctorInfo(){
        System.out.println("Doctor ID: " + doctorID);
        System.out.println("Specialization: " + specialization);
        System.out.println("Availability: " + availability);
    }

    @Override
    public String toString(){
        return "Doctor: " + "DoctorID = " + doctorID + '\n' + "Specialization " + specialization + '\n' + "Availabiltity " + availability;
    }

    public static class DoctorBuilder{
        private String doctorID;
        private String specialization;
        private boolean availability;

        public DoctorBuilder setDoctorID(String doctorID){
            this.doctorID = doctorID;
            return this;
        }
        public DoctorBuilder setSpecialization(String specialization){
            this.specialization = specialization;
            return this;
        }
        public DoctorBuilder isAvailable(boolean availability){
            this.availability = availability;
            return this;
        }
        public Doctor build(){
            return new Doctor(this);
        }
    }
}
