package vn.edu.iuh.fit;

import com.neovisionaries.i18n.CountryCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import vn.edu.iuh.fit.models.Address;
import vn.edu.iuh.fit.models.Candidate;
import vn.edu.iuh.fit.repositories.AddressRepository;
import vn.edu.iuh.fit.repositories.CandidateRepository;

import java.time.LocalDate;
import java.util.Random;
@SpringBootApplication
public class DinhVanHungWeek05Application {
    public static void main(String[] args) {
        SpringApplication.run(DinhVanHungWeek05Application.class, args);
    }
    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private AddressRepository addressRepository;
//    @Bean
//    CommandLineRunner initData() {
//        return args -> {
//            Random rnd = new Random();
//            for (int i = 1; i <= 1000; i++) {
//                // Create an Address instance
//                Address address = new Address();
//                address.setStreet(rnd.nextInt(999) + " Street");
//                address.setCity("HCM");
//                address.setCountry((short) 84); // Country code for Vietnam
//                address.setNumber(rnd.nextInt(1000) + "");
//                address.setZipcode(rnd.nextInt(10000) + 70000 + "");
//
//                // Save Address to ensure it has an ID
//                address = addressRepository.save(address);
//
//                // Create Candidate and associate it with the saved Address
//                Candidate candidate = new Candidate();
//                candidate.setFullName("Name #" + i);
//                candidate.setDob(LocalDate.of(1998, rnd.nextInt(12) + 1, rnd.nextInt(28) + 1));
//                candidate.setAddress(address); // Set the saved Address instance
//                candidate.setPhone(String.valueOf(rnd.nextLong(1111111111L, 9999999999L)));
//                candidate.setEmail("email_" + i + "@gmail.com");
//
//                // Save Candidate
//                candidateRepository.save(candidate);
//                System.out.println("Added: " + candidate);
//            }
//        };
//    }

}
