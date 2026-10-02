package com.matrimony.backend.config;

import com.matrimony.backend.entity.User;
import com.matrimony.backend.entity.SuccessStory;
import com.matrimony.backend.enums.AccountStatus;
import com.matrimony.backend.enums.Role;
import com.matrimony.backend.enums.SuccessStoryStatus;
import com.matrimony.backend.entity.MatrimonyProfile;
import com.matrimony.backend.enums.Gender;
import com.matrimony.backend.enums.MaritalStatus;
import com.matrimony.backend.enums.ProfileCreatedFor;
import com.matrimony.backend.enums.ProfileStatus;
import com.matrimony.backend.repository.UserRepository;
import com.matrimony.backend.repository.SuccessStoryRepository;
import com.matrimony.backend.repository.MatrimonyProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevelopmentDataInitializer implements CommandLineRunner {
    private final AppProperties properties;
    private final UserRepository userRepository;
    private final SuccessStoryRepository successStoryRepository;
    private final MatrimonyProfileRepository matrimonyProfileRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String password = properties.development().adminPassword();
        if (!StringUtils.hasText(password)) {
            return;
        }
        User superAdmin = null;
        if (!userRepository.existsByEmailIgnoreCase("superadmin@buddhistmatrimony.com")) {
            superAdmin = new User();
            superAdmin.setMatrimonyId("SUPERADMIN");
            superAdmin.setEmail("superadmin@buddhistmatrimony.com");
            superAdmin.setCountryCode("+91");
            superAdmin.setMobileNumber("8888888888");
            superAdmin.setPasswordHash(passwordEncoder.encode("password123"));
            superAdmin.setRole(Role.ADMIN);
            superAdmin.setAccountStatus(AccountStatus.ACTIVE);
            superAdmin.setEmailVerified(true);
            superAdmin.setMobileVerified(true);
            superAdmin.setTermsAcceptedAt(LocalDateTime.now());
            superAdmin.setPrivacyPolicyAcceptedAt(LocalDateTime.now());
            superAdmin.setTermsVersion("2026.08");
            superAdmin.setPrivacyPolicyVersion("2026.08");
            superAdmin = userRepository.save(superAdmin);
        } else {
            superAdmin = userRepository.findByEmailIgnoreCase("superadmin@buddhistmatrimony.com").orElse(null);
        }

        if (successStoryRepository.count() == 0) {
            record SeedStory(String bride, String groom, String story, String photo, int monthsAgo, SuccessStoryStatus status) {}

            java.util.List<SeedStory> seeds = java.util.List.of(
                new SeedStory("Priya Shinde", "Amit Kamble",
                    "We connected through Buddhist Matrimony in January 2026. After exchanging a few heartfelt messages, we realized we shared the same values — compassion, simplicity, and faith in the Dhamma. Our families met in March, and we were married in April with the blessings of our Sangha. Thank you for bringing us together!",
                    "https://images.unsplash.com/photo-1537907510278-9f16bfb64e31?w=800&q=80", 3, SuccessStoryStatus.APPROVED),
                new SeedStory("Sneha Bodkhe", "Rahul Meshram",
                    "I had almost given up on finding a life partner who shared my Buddhist principles. Then I found Rahul on this platform. We spoke for weeks before meeting, and every conversation deepened my respect for him. Our wedding was a simple and meaningful ceremony surrounded by monks and family.",
                    "https://images.unsplash.com/photo-1519741497674-611481863552?w=800&q=80", 5, SuccessStoryStatus.APPROVED),
                new SeedStory("Neha Gaikwad", "Vikram Pawar",
                    "Buddhist Matrimony matched us perfectly — both from similar backgrounds, both committed to the path. Vikram reached out first and his first message was about the Dhammapada. We knew from day one this was something special. Today we are married and practising the Dhamma together.",
                    "https://images.unsplash.com/photo-1511285560929-80b456fea0bc?w=800&q=80", 2, SuccessStoryStatus.APPROVED),
                new SeedStory("Deepa Salve", "Suresh Rathod",
                    "Our parents were sceptical about online matrimony at first. But this platform's verified profiles gave them confidence. Suresh and I met here, spoke honestly, and decided to marry within six months. The platform made the entire journey safe and trustworthy.",
                    "https://images.unsplash.com/photo-1606800052052-a08af7148866?w=800&q=80", 6, SuccessStoryStatus.APPROVED),
                new SeedStory("Anjali Jadhav", "Rohan Chavhan",
                    "I am writing this with so much gratitude. Rohan and I found each other on this platform when both of us had profiles for less than a month. We bonded over our shared love of meditation retreats and Ambedkarite literature. We got married in December 2025 and are now building our home together.",
                    "https://images.unsplash.com/photo-1591604466107-ec97de577aff?w=800&q=80", 8, SuccessStoryStatus.APPROVED),
                new SeedStory("Ritu Lonare", "Karan Bharambe",
                    "Karan and I connected instantly. He was honest, grounded, and genuinely Buddhist in his way of life. The platform helped us find each other across cities — he is from Nagpur and I am from Pune. Despite the distance, we made it work and got married in a beautiful ceremony.",
                    "https://images.unsplash.com/photo-1529636798458-92182e662485?w=800&q=80", 10, SuccessStoryStatus.APPROVED),
                new SeedStory("Pooja Nandagawali", "Abhishek Tekade",
                    "We both practise Vipassana and found that this was the very first thing we talked about. Abhishek was kind, patient, and sincere. This platform provided a safe space to explore compatibility without pressure. We married in a Dhamma ceremony in February 2026.",
                    "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=800&q=80", 4, SuccessStoryStatus.APPROVED),
                new SeedStory("Divya Wagde", "Manish Waghmare",
                    "We spent three months communicating through the platform's messaging system before exchanging numbers. Manish was respectful of boundaries and our families appreciated that. Our wedding in November 2025 was one of the happiest days of our lives.",
                    "https://images.unsplash.com/photo-1465495976277-4387d4b0b4c6?w=800&q=80", 9, SuccessStoryStatus.APPROVED),
                new SeedStory("Kirti Ambhore", "Aditya Gonnade",
                    "As a working woman with high values, I was looking for a partner who respects equality. Aditya was exactly that person. He believes in Babasaheb's vision of dignity and equality. Buddhist Matrimony gave us the perfect platform to discover each other.",
                    "https://images.unsplash.com/photo-1550005809-91ad75fb315f?w=800&q=80", 7, SuccessStoryStatus.APPROVED),
                new SeedStory("Komal Rokade", "Sanjay Ingole",
                    "Sanjay and I matched on every level — education, values, and life goals. Our families were thrilled when they met. The entire process from first message to marriage took just five months. We are incredibly thankful to this platform.",
                    "https://images.unsplash.com/photo-1523438885200-e635ba2c371e?w=800&q=80", 12, SuccessStoryStatus.APPROVED),
                new SeedStory("Asha Kamble", "Vijay Sonule",
                    "I matched with Vijay and within our very first conversation I felt a sense of calm and clarity. He was transparent about his background and expectations. We are now happily married and expecting our first child. Buddhist Matrimony truly changed our lives.",
                    "https://images.unsplash.com/photo-1583939003579-730e3918a45a?w=800&q=80", 1, SuccessStoryStatus.APPROVED),
                new SeedStory("Jyoti Kendre", "Rajesh Bansod",
                    "My parents had been searching for a suitable match for over a year. Within two weeks of joining this platform, Rajesh's profile came up. Everything matched — community, education, and values. We are now happily settled in Mumbai.",
                    "https://images.unsplash.com/photo-1580136579312-94651dfd596d?w=800&q=80", 14, SuccessStoryStatus.APPROVED),
                new SeedStory("Meena Dhole", "Sunil Bhalerao",
                    "Sunil and I both attended the same Dhamma convention but never met! It was Buddhist Matrimony that finally connected us. We laugh about it every day. The platform truly has a way of finding the right people for you.",
                    "https://images.unsplash.com/photo-1469371670807-013ccf25f16a?w=800&q=80", 11, SuccessStoryStatus.APPROVED),
                new SeedStory("Rekha Narwade", "Anil Khobragade",
                    "After two failed matches through family introductions, I turned to Buddhist Matrimony. Anil's profile was genuine and detailed. We spoke for six weeks before meeting. The rest is a beautiful chapter of our lives.",
                    "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=800&q=80", 16, SuccessStoryStatus.APPROVED),
                new SeedStory("Shweta Ingle", "Deepak Chavare",
                    "Deepak proposed during a peaceful walk at Deekshabhoomi, Nagpur. Our love story began here, on this platform, where verified profiles and thoughtful filters helped us find each other with complete peace of mind. Jai Bhim!",
                    "https://images.unsplash.com/photo-1476703993599-0035a21b17a9?w=800&q=80", 18, SuccessStoryStatus.APPROVED),
                new SeedStory("Preeti Mohod", "Sandip Dahake",
                    "We are both passionate about social work in the Buddhist community. Sandip and I met through this platform and immediately found a common purpose in life. Our marriage is not just a union of two people but two missions.",
                    "https://images.unsplash.com/photo-1516589178581-6cd7833ae3b2?w=800&q=80", 15, SuccessStoryStatus.APPROVED),
                new SeedStory("Kiran Wankhede", "Ashish Gawai",
                    "Ashish was the third profile I contacted. His humility and depth stood out. We met for coffee in Pune after two weeks of chatting. By the second meeting, we knew we were meant for each other. Simple, honest, beautiful.",
                    "https://images.unsplash.com/photo-1522673607200-164d1b6ce486?w=800&q=80", 20, SuccessStoryStatus.APPROVED),
                new SeedStory("Sonia Raut", "Ajay Uikey",
                    "Finding a partner who values the Triratna was my only criteria. Ajay is everything I prayed for — kind, principled, and devoted to the path. Buddhist Matrimony made what seemed impossible, possible. We are married and living in Pune.",
                    "https://images.unsplash.com/photo-1518621736915-f3b1c41bfd00?w=800&q=80", 13, SuccessStoryStatus.APPROVED),
                new SeedStory("Swati Dhote", "Harish Kokate",
                    "Harish and I spoke every day for two months before deciding to meet. Our families bonded instantly. The wedding was small and meaningful, with monks reciting suttas and blessings from both families. We could not be happier.",
                    "https://images.unsplash.com/photo-1504196606672-aef5c9cefc92?w=800&q=80", 17, SuccessStoryStatus.APPROVED),
                new SeedStory("Rashmi Padole", "Pankaj Baghele",
                    "Pankaj's first message to me was a quote from Dr. Ambedkar — that told me everything. We spent months getting to know each other deeply. Buddhist Matrimony gave us the right environment to build trust. Today we are a family.",
                    "https://images.unsplash.com/photo-1512850183-6d7990f42385?w=800&q=80", 19, SuccessStoryStatus.APPROVED)
            );

            for (SeedStory s : seeds) {
                SuccessStory story = new SuccessStory();
                story.setBrideName(s.bride());
                story.setGroomName(s.groom());
                story.setStory(s.story());
                story.setMarriageDate(LocalDate.now().minusMonths(s.monthsAgo()));
                story.setStatus(s.status());
                story.setSubmittedBy(superAdmin);
                story.setPhotoUrl(s.photo());
                if (s.status() == SuccessStoryStatus.APPROVED) {
                    story.setApprovedBy(superAdmin);
                    story.setApprovedAt(LocalDateTime.now().minusDays(s.monthsAgo()));
                }
                successStoryRepository.save(story);
            }
        }

        if (matrimonyProfileRepository.count() < 20) {
            String[] firstNames = {"Raj", "Sneha", "Amit", "Priya", "Rahul", "Neha", "Suresh", "Deepa", "Rohan", "Anjali", "Karan", "Ritu", "Abhishek", "Pooja", "Manish", "Divya", "Aditya", "Kirti", "Sanjay", "Komal"};
            String[] lastNames = {"Kamble", "Bodkhe", "Meshram", "Gaikwad", "Pawar", "Salve", "Rathod", "Jadhav", "Chavhan", "Lonare", "Bharambe", "Nandagawali", "Tekade", "Wagde", "Waghmare", "Ambhore", "Gonnade", "Rokade", "Ingole", "Sonule"};
            Gender[] genders = {Gender.MALE, Gender.FEMALE, Gender.MALE, Gender.FEMALE, Gender.MALE, Gender.FEMALE, Gender.MALE, Gender.FEMALE, Gender.MALE, Gender.FEMALE, Gender.MALE, Gender.FEMALE, Gender.MALE, Gender.FEMALE, Gender.MALE, Gender.FEMALE, Gender.MALE, Gender.FEMALE, Gender.MALE, Gender.FEMALE};

            for (int i = 0; i < 20; i++) {
                User user = new User();
                String matId = "BM" + (1000 + i);
                user.setMatrimonyId(matId);
                user.setEmail("user" + i + "@example.com");
                user.setCountryCode("+91");
                user.setMobileNumber("99999999" + String.format("%02d", i));
                user.setPasswordHash(passwordEncoder.encode("password123"));
                user.setRole(Role.USER);
                user.setAccountStatus(AccountStatus.ACTIVE);
                user.setEmailVerified(true);
                user.setMobileVerified(true);
                user.setTermsAcceptedAt(LocalDateTime.now());
                user.setPrivacyPolicyAcceptedAt(LocalDateTime.now());
                user.setTermsVersion("2026.08");
                user.setPrivacyPolicyVersion("2026.08");
                user = userRepository.save(user);

                MatrimonyProfile profile = new MatrimonyProfile();
                profile.setUser(user);
                profile.setProfileCreatedFor(ProfileCreatedFor.MYSELF);
                profile.setFirstName(firstNames[i]);
                profile.setLastName(lastNames[i]);
                profile.setGender(genders[i]);
                profile.setDateOfBirth(LocalDate.of(1990 + (i % 10), 1 + (i % 12), 1 + (i % 28)));
                profile.setHeightInCm(160 + (i % 20));
                profile.setWeightInKg(60 + (i % 20));
                profile.setMaritalStatus(MaritalStatus.NEVER_MARRIED);
                profile.setReligion("Buddhism");
                profile.setCommunity("Buddhist");
                profile.setState("Maharashtra");
                profile.setCity((i % 2 == 0) ? "Pune" : "Nagpur");
                profile.setProfileStatus(ProfileStatus.ACTIVE);
                profile.setProfileCompleteness(100);
                matrimonyProfileRepository.save(profile);
            }
        }
    }
}

