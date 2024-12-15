-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               11.2.0-MariaDB - mariadb.org binary distribution
-- Server OS:                    Win64
-- HeidiSQL Version:             12.3.0.6589
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for works
CREATE DATABASE IF NOT EXISTS `works` /*!40100 DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci */;
USE `works`;

-- Dumping structure for table works.address
CREATE TABLE IF NOT EXISTS `address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `street` varchar(150) DEFAULT NULL,
  `city` varchar(50) DEFAULT NULL,
  `country` smallint(6) DEFAULT NULL CHECK (`country` between 0 and 201),
  `number` varchar(20) DEFAULT NULL,
  `zipcode` varchar(7) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Data exporting was unselected.

-- Dumping structure for table works.candidate
CREATE TABLE IF NOT EXISTS `candidate` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dob` date NOT NULL,
  `email` varchar(255) NOT NULL,
  `full_name` varchar(255) NOT NULL,
  `phone` varchar(15) NOT NULL,
  `address` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_qfut8ruekode092nlkipgl09g` (`email`),
  UNIQUE KEY `UK_9i5yt1gvm0chg5e10qkns7tll` (`phone`),
  UNIQUE KEY `UK_m8qhlm4wu215gr34dhxp0dour` (`address`),
  CONSTRAINT `FKa8gnyyhbb2qnhp94grci1n0o9` FOREIGN KEY (`address`) REFERENCES `address` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Data exporting was unselected.

-- Dumping structure for table works.candidate_skill
CREATE TABLE IF NOT EXISTS `candidate_skill` (
  `more_infos` varchar(1000) DEFAULT NULL,
  `skill_level` tinyint(4) NOT NULL CHECK (`skill_level` between 0 and 2),
  `skill_id` bigint(20) NOT NULL,
  `can_id` bigint(20) NOT NULL,
  PRIMARY KEY (`can_id`,`skill_id`),
  KEY `FKb7cxhiqhcah7c20a2cdlvr1f8` (`skill_id`),
  CONSTRAINT `FKb0m5tm3fi0upa3b3kjx3vrlxs` FOREIGN KEY (`can_id`) REFERENCES `candidate` (`id`),
  CONSTRAINT `FKb7cxhiqhcah7c20a2cdlvr1f8` FOREIGN KEY (`skill_id`) REFERENCES `skill` (`skill_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Data exporting was unselected.

-- Dumping structure for table works.company
CREATE TABLE IF NOT EXISTS `company` (
  `comp_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `about` varchar(2000) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `comp_name` varchar(255) NOT NULL,
  `phone` varchar(255) NOT NULL,
  `web_url` varchar(255) DEFAULT NULL,
  `address` bigint(20) NOT NULL,
  PRIMARY KEY (`comp_id`),
  UNIQUE KEY `UK_rvp2hunsq4sgmpxe3a7i1ym3m` (`address`),
  CONSTRAINT `FKd5occp4cjwihejbxdbpvkp5tv` FOREIGN KEY (`address`) REFERENCES `address` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Data exporting was unselected.

-- Dumping structure for table works.job
CREATE TABLE IF NOT EXISTS `job` (
  `job_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `job_desc` varchar(2000) NOT NULL,
  `job_name` varchar(255) NOT NULL,
  `company` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`job_id`),
  KEY `FKbaqlvluu78phmo9ld89um7wnm` (`company`),
  CONSTRAINT `FKbaqlvluu78phmo9ld89um7wnm` FOREIGN KEY (`company`) REFERENCES `company` (`comp_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Data exporting was unselected.

-- Dumping structure for table works.job_skill
CREATE TABLE IF NOT EXISTS `job_skill` (
  `more_infos` varchar(1000) DEFAULT NULL,
  `skill_level` tinyint(4) NOT NULL CHECK (`skill_level` between 0 and 2),
  `job_id` bigint(20) NOT NULL,
  `skill_id` bigint(20) NOT NULL,
  PRIMARY KEY (`job_id`,`skill_id`),
  KEY `FKj33qbbf3vk1lvhqpcosnh54u1` (`skill_id`),
  CONSTRAINT `FK9ix4wg520ii2gu2felxdhdup6` FOREIGN KEY (`job_id`) REFERENCES `job` (`job_id`),
  CONSTRAINT `FKj33qbbf3vk1lvhqpcosnh54u1` FOREIGN KEY (`skill_id`) REFERENCES `skill` (`skill_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Data exporting was unselected.

-- Dumping structure for table works.skill
CREATE TABLE IF NOT EXISTS `skill` (
  `skill_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `skill_description` varchar(255) DEFAULT NULL,
  `skill_name` varchar(255) DEFAULT NULL,
  `type` tinyint(4) DEFAULT NULL CHECK (`type` between 0 and 2),
  PRIMARY KEY (`skill_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
-- Data exporting was unselected.
SELECT * FROM address;
CREATE TABLE SPRING_SESSION (
    PRIMARY_ID CHAR(36) NOT NULL,
    SESSION_ID CHAR(36) NOT NULL,
    CREATION_TIME BIGINT NOT NULL,
    LAST_ACCESS_TIME BIGINT NOT NULL,
    MAX_INACTIVE_INTERVAL INT NOT NULL,
    EXPIRY_TIME BIGINT NOT NULL,
    PRINCIPAL_NAME VARCHAR(100),
    PRIMARY KEY (PRIMARY_ID)
);

CREATE UNIQUE INDEX SPRING_SESSION_IX1 ON SPRING_SESSION (SESSION_ID);
CREATE INDEX SPRING_SESSION_IX2 ON SPRING_SESSION (EXPIRY_TIME);
CREATE INDEX SPRING_SESSION_IX3 ON SPRING_SESSION (PRINCIPAL_NAME);

CREATE TABLE SPRING_SESSION_ATTRIBUTES (
    SESSION_PRIMARY_ID CHAR(36) NOT NULL,
    ATTRIBUTE_NAME VARCHAR(200) NOT NULL,
    ATTRIBUTE_BYTES BLOB NOT NULL,
    PRIMARY KEY (SESSION_PRIMARY_ID, ATTRIBUTE_NAME),
    CONSTRAINT SPRING_SESSION_ATTRIBUTES_FK FOREIGN KEY (SESSION_PRIMARY_ID) REFERENCES SPRING_SESSION (PRIMARY_ID) ON DELETE CASCADE
);
/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;

--them cac bang moi vao 

-- Tạo bảng Experience
CREATE TABLE experience (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  from_date DATE NOT NULL,
  to_date DATE,
  role VARCHAR(255),
  work_description VARCHAR(2000),
  company_name VARCHAR(255),
  candidate_id BIGINT NOT NULL,
  CONSTRAINT FK_experience_candidate FOREIGN KEY (candidate_id) REFERENCES candidate (id)
);



-- Tạo bảng SkillLevel
CREATE TABLE skill_level (
  level_id TINYINT PRIMARY KEY,
  level_name VARCHAR(50) NOT NULL
);
--cap nhat skill LEVEL 
- Drop the existing CHECK constraint on skill_level (if applicable)
ALTER TABLE `job_skill` DROP CHECK `skill_level`;

-- Add the new CHECK constraint for skill_level
ALTER TABLE `job_skill`
ADD CONSTRAINT `chk_skill_level_range` CHECK (`skill_level` BETWEEN 0 AND 4);

-- Tạo bảng SkillType
CREATE TABLE skill_type (
  type_id TINYINT PRIMARY KEY,
  type_name VARCHAR(50) NOT NULL
);

-- Create SkillLevel table
CREATE TABLE IF NOT EXISTS `skill_level` (
  `id` TINYINT PRIMARY KEY AUTO_INCREMENT,
  `name` ENUM('MASTER', 'BEGINNER', 'ADVANCED', 'PROFESSIONAL', 'INTERMEDIATE') NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Create JobSkill table with reference to SkillLevel
CREATE TABLE IF NOT EXISTS `job_skill` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `more_infos` VARCHAR(1000) DEFAULT NULL,
  `job_id` BIGINT NOT NULL,
  `skill_id` BIGINT NOT NULL,
  `skill_level_id` TINYINT NOT NULL,
  CONSTRAINT `FK_job_skill_job` FOREIGN KEY (`job_id`) REFERENCES `job` (`job_id`),
  CONSTRAINT `FK_job_skill_skill` FOREIGN KEY (`skill_id`) REFERENCES `skill` (`skill_id`),
  CONSTRAINT `FK_job_skill_skill_level` FOREIGN KEY (`skill_level_id`) REFERENCES `skill_level` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Insert data into SkillLevel table
INSERT INTO `skill_level` (`name`) VALUES
('MASTER'),
('BEGINNER'),
('ADVANCED'),
('PROFESSIONAL'),
('INTERMEDIATE');

-- Insert sample data into JobSkill table
INSERT INTO `job_skill` (`more_infos`, `job_id`, `skill_id`, `skill_level_id`) VALUES
('Expert in Java', 1, 1, 1), -- MASTER
('Beginner level for JavaScript', 2, 2, 2), -- BEGINNER
('Advanced database management skills', 3, 3, 3); -- ADVANCED
-- Bảng company 
INSERT INTO company (about, email, comp_name, phone, web_url, address) VALUES 
('Leading tech company', 'info@techcorp.com', 'TechCorp', '1234567890', 'http://techcorp.com', 1),
('Global financial services', 'contact@finbank.com', 'FinBank', '0987654321', 'http://finbank.com', 2),
('E-commerce giant', 'support@shopnow.com', 'ShopNow', '1122334455', 'http://shopnow.com', 3),
('Cloud solutions provider', 'cloud@cloudserv.com', 'CloudServ', '2233445566', 'http://cloudserv.com', 4),
('Automotive manufacturer', 'auto@carworld.com', 'CarWorld', '3344556677', 'http://carworld.com', 5);

-- Bảng skill 
INSERT INTO skill (skill_description, skill_name, type) VALUES 
('Proficient in Java programming', 'Java', 0),
('Expert in Web Development', 'Web Development', 0),
('Experienced in Database Management', 'Database Management', 1),
('Skilled in Cloud Computing', 'Cloud Computing', 1),
('Advanced in Cybersecurity', 'Cybersecurity', 2);

-- Bảng candidate_skill 
INSERT INTO candidate_skill (more_infos, skill_level, skill_id, can_id) VALUES 
('5 years of experience', 2, 1, 1),
('Worked on multiple web projects', 2, 2, 1),
('Certified database manager', 1, 3, 2),
('Cloud expert certified', 2, 4, 3),
('Managed security for financial services', 2, 5, 4);

-- Bảng job_skill 
INSERT INTO job_skill (more_infos, skill_level, job_id, skill_id) VALUES 
('Advanced knowledge required', 2, 1, 1),
('Strong web development skills needed', 2, 2, 2),
('Database administration expertise', 2, 3, 3),
('Experience with cloud services', 2, 4, 4),
('Strong cybersecurity background', 2, 5, 5);

-- Bảng job 
INSERT INTO job (job_desc, job_name, company) VALUES 
('Responsible for developing and implementing scalable Java applications.', 'Java Developer', 1),
('Designing user-friendly web applications and interfaces.', 'Frontend Developer', 2),
('Maintaining and optimizing database performance for high-traffic applications.', 'Database Administrator', 3),
('Setting up and managing cloud infrastructure and services.', 'Cloud Engineer', 4),
('Identifying and mitigating security vulnerabilities across systems.', 'Cybersecurity Analyst', 5),
('Building and integrating REST APIs for various applications.', 'Backend Developer', 1),
('Providing technical support and troubleshooting for client applications.', 'IT Support Specialist', 2),
('Analyzing business requirements and translating them into technical solutions.', 'Business Analyst', 3),
('Leading project teams to deliver software projects on time and within scope.', 'Project Manager', 4),
('Creating and maintaining automated test scripts for web applications.', 'QA Engineer', 5),
('Developing and deploying machine learning models for data analysis.', 'Data Scientist', 1),
('Designing engaging and interactive mobile applications.', 'Mobile App Developer', 2),
('Implementing and maintaining CI/CD pipelines for software deployment.', 'DevOps Engineer', 3),
('Writing and optimizing SQL queries for database management.', 'SQL Developer', 4),
('Developing blockchain solutions for secure and transparent transactions.', 'Blockchain Developer', 5),
('Analyzing data to extract insights and support decision-making.', 'Data Analyst', 1),
('Creating and executing marketing campaigns on social media platforms.', 'Digital Marketing Specialist', 2),
('Managing e-commerce website and updating product listings.', 'E-commerce Manager', 3),
('Designing logos, banners, and other marketing materials.', 'Graphic Designer', 4),
('Researching market trends to identify new business opportunities.', 'Market Research Analyst', 5),
('Assisting in recruitment, onboarding, and employee relations.', 'HR Specialist', 1),
('Handling customer inquiries and resolving issues effectively.', 'Customer Service Representative', 2),
('Coordinating logistics and inventory management for products.', 'Logistics Coordinator', 3),
('Managing the company’s financial transactions and records.', 'Accountant', 4),
('Creating compelling content for websites and blogs.', 'Content Writer', 5),
('Editing and publishing video content for marketing campaigns.', 'Video Editor', 1),
('Developing product design and functionality based on customer feedback.', 'Product Designer', 2),
('Assisting the legal team with document preparation and compliance.', 'Legal Assistant', 3),
('Researching and developing new software tools for automation.', 'Software Engineer', 4),
('Managing online advertisements and pay-per-click campaigns.', 'PPC Specialist', 5),
('Ensuring the stability and performance of IT infrastructure.', 'Network Administrator', 1),
('Developing game concepts and implementing mechanics.', 'Game Developer', 2),
('Planning and executing sales strategies to increase revenue.', 'Sales Manager', 3),
('Managing the overall brand identity and public relations.', 'Brand Manager', 4),
('Providing cybersecurity training and awareness programs.', 'Security Consultant', 5),
('Improving user experience and accessibility of applications.', 'UX Designer', 1),
('Managing the creation and deployment of email marketing campaigns.', 'Email Marketing Specialist', 2),
('Developing AI models to enhance user experience.', 'AI Engineer', 3),
('Collaborating with clients to gather and document requirements.', 'Consultant', 4),
('Providing training on software tools and systems.', 'Trainer', 5),
('Ensuring that software adheres to legal and regulatory standards.', 'Compliance Officer', 1),
('Coordinating event planning and execution for corporate events.', 'Event Planner', 2),
('Overseeing the creation and implementation of ERP systems.', 'ERP Consultant', 3),
('Designing interactive e-learning courses and modules.', 'Instructional Designer', 4),
('Managing corporate communications and press releases.', 'Communications Manager', 5),
('Developing systems to enhance information security protocols.', 'Information Security Analyst', 1),
('Providing advice on company policies and employee performance.', 'HR Consultant', 2),
('Maintaining and monitoring network systems.', 'Systems Administrator', 3),
('Creating storyboards and scripts for animation projects.', 'Animator', 4),
('Conducting training programs on health and safety standards.', 'Safety Trainer', 5);

--test 
INSERT INTO job (job_desc, job_name, company) 
VALUES 
('Develop and maintain web applications', 'Web Developer', 101),
('Manage IT infrastructure', 'IT Manager', 101);


-- Sử lý AUTHENTICATION 
-- Thêm các role vào bảng role
INSERT INTO role (role_name) VALUES ('ROLE_CANDIDATE');
INSERT INTO role (role_name) VALUES ('ROLE_RECRUITER');
INSERT INTO role (role_name) VALUES ('ROLE_ADMIN');

-- Thêm các account vào bảng account
-- Giả sử các mật khẩu đã được mã hóa trước bằng BCrypt
-- Thêm dữ liệu vào bảng account
INSERT INTO `account` (username, password, role_id, candidate_id, company_id, enabled) VALUES 
('candidate1', '$2a$10$CW4GID5sWdQHKyMEOgSgYOF8tVY53yTDe4FEOT6hB1XTy0VoLMGtu', 1, 1, NULL, TRUE),  -- ROLE_CANDIDATE
('recruiter1', '$2a$10$CW4GID5sWdQHKyMEOgSgYOF8tVY53yTDe4FEOT6hB1XTy0VoLMGtu', 2, NULL, 1, TRUE),  -- ROLE_RECRUITER
('admin1', '$2a$10$CW4GID5sWdQHKyMEOgSgYOF8tVY53yTDe4FEOT6hB1XTy0VoLMGtu', 3, NULL, NULL, TRUE);  -- ROLE_ADMIN

INSERT INTO `account` (username, password, role_id, candidate_id, company_id, enabled) VALUES 
('candidate2', '$2a$10$CW4GID5sWdQHKyMEOgSgYOF8tVY53yTDe4FEOT6hB1XTy0VoLMGtu', 1, 10, NULL, TRUE);
INSERT INTO `account` (username, password, role_id, candidate_id, company_id, enabled) VALUES 
('candidate3', '$2a$10$CW4GID5sWdQHKyMEOgSgYOF8tVY53yTDe4FEOT6hB1XTy0VoLMGtu', 1, NULL, NULL, TRUE);  -- ROLE_CANDIDATE


SHOW CREATE TABLE account;


-- Liên kết các account với candidate hoặc recruiter nếu cần
-- Giả sử `candidate_user` là ứng viên, `recruiter_user` là nhà tuyển dụng
INSERT INTO candidate (id, full_name, dob, email, phone, address)
    VALUES (1, 'Candidate One', '1990-01-01', 'candidate@example.com', '123456789', 1);

INSERT INTO company (comp_id, comp_name, email, phone, address)
    VALUES (1, 'Recruiter Company', 'recruiter@example.com', '987654321', 2);


CREATE TABLE IF NOT EXISTS `role` (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(50) NOT NULL UNIQUE, -- Ví dụ: 'CANDIDATE', 'RECRUITER', 'ADMIN'
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=LATIN1_SWEDISH_CI;
CREATE TABLE IF NOT EXISTS `account` (
  `account_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL UNIQUE,
  `password` varchar(255) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  `candidate_id` bigint(20) DEFAULT NULL, -- Liên kết với bảng candidate nếu là ứng viên
  `company_id` bigint(20) DEFAULT NULL, -- Liên kết với bảng company nếu là nhà tuyển dụng
  PRIMARY KEY (`account_id`),
  UNIQUE KEY `UK_account_candidate` (`candidate_id`),
  UNIQUE KEY `UK_account_company` (`company_id`),
  CONSTRAINT `FK_account_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`),
  CONSTRAINT `FK_account_candidate` FOREIGN KEY (`candidate_id`) REFERENCES `candidate` (`id`),
  CONSTRAINT `FK_account_company` FOREIGN KEY (`company_id`) REFERENCES `company` (`comp_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=LATIN1_SWEDISH_CI;
ALTER TABLE account ADD COLUMN enabled BOOLEAN DEFAULT TRUE;


SELECT * FROM account;

--kiem tra du lieu 
SELECT a.username, r.role_name, c.full_name AS candidate_name, co.comp_name AS company_name
FROM account a
LEFT JOIN role r ON a.role_id = r.role_id
LEFT JOIN candidate c ON a.candidate_id = c.id
LEFT JOIN company co ON a.company_id = co.comp_id;

--them job cho nha tuyen dung cua cong ty dau tien 
INSERT INTO job (job_desc, job_name, company)
VALUES
('Develop and maintain web applications using Java and Spring Framework', 'Java Developer', 1),
('Design and develop database systems for e-commerce platforms', 'Database Administrator', 1),
('Lead and manage a team of software developers to deliver high-quality projects', 'Software Team Lead', 1),
('Create user-friendly UI/UX designs for mobile and web applications', 'UI/UX Designer', 1),
('Develop automation scripts for testing web and mobile applications', 'Automation Tester', 1);

--them cadidate vao vi tri tuyen dung 
INSERT INTO candidate_job (candidate_id, job_id) VALUES
(1, 1), 
(2,1), 
(3, 1), 
(4, 1), 
(5, 1);

--test danh gia 
UPDATE candidate
SET email = 'dinhhung1212200303@gmail.com'
WHERE id = 1;

CREATE TABLE IF NOT EXISTS `candidate_skill` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `more_infos` VARCHAR(1000) DEFAULT NULL,
  `candidate_id` BIGINT NOT NULL,
  `skill_id` BIGINT NOT NULL,
  `skill_level_id` TINYINT NOT NULL,
  CONSTRAINT `FK_candidate_skill_candidate` FOREIGN KEY (`candidate_id`) REFERENCES `candidate` (`id`),
  CONSTRAINT `FK_candidate_skill_skill` FOREIGN KEY (`skill_id`) REFERENCES `skill` (`skill_id`),
  CONSTRAINT `FK_candidate_skill_level` FOREIGN KEY (`skill_level_id`) REFERENCES `skill_level` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Insert data into CandidateSkill table
INSERT INTO `candidate_skill` (`more_infos`, `candidate_id`, `skill_id`, `skill_level_id`) VALUES
('5 years of experience in Java', 1, 1, 1), -- MASTER
('Basic understanding of JavaScript', 2, 2, 2), -- BEGINNER
('Advanced skills in database management', 3, 3, 3); -- ADVANCED

CREATE TABLE IF NOT EXISTS `skill_type` (
  `id` TINYINT PRIMARY KEY AUTO_INCREMENT,
  `name` ENUM('SOFT_SKILL', 'UNSPECIFIC', 'TECHNICAL_SKILL') NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;


CREATE TABLE IF NOT EXISTS `skill` (
  `skill_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `skill_name` VARCHAR(255) NOT NULL,
  `skill_description` VARCHAR(1000),
  `skill_type_id` TINYINT NOT NULL,
  CONSTRAINT `FK_skill_skill_type` FOREIGN KEY (`skill_type_id`) REFERENCES `skill_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
// bang email_log 
CREATE TABLE email_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, -- ID tự tăng
    candidate_id BIGINT NOT NULL,         -- ID ứng viên
    job_id BIGINT NOT NULL,               -- ID công việc
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Thời gian gửi
    status VARCHAR(50) DEFAULT 'SUCCESS', -- Trạng thái gửi (SUCCESS hoặc FAILURE)
    message TEXT,                         -- Thông báo lỗi (nếu có)
    FOREIGN KEY (candidate_id) REFERENCES candidate(id) ON DELETE CASCADE, -- Khóa ngoại đến bảng candidate
    FOREIGN KEY (job_id) REFERENCES job(job_id) ON DELETE CASCADE          -- Khóa ngoại đến bảng job
);


INSERT INTO `skill_type` (`name`) VALUES
('SOFT_SKILL'),
('UNSPECIFIC'),
('TECHNICAL_SKILL');


INSERT INTO `skill` (`skill_name`, `skill_description`, `skill_type_id`) VALUES
('Teamwork', 'Ability to work effectively in a team environment', 1), -- SOFT_SKILL
('Problem Solving', 'Capable of solving complex problems efficiently', 1), -- SOFT_SKILL
('Project Management', 'Planning and managing resources to achieve goals', 2), -- UNSPECIFIC
('Java Programming', 'Proficiency in Java for backend development', 3), -- TECHNICAL_SKILL
('Database Management', 'Designing and maintaining relational databases', 3); -- TECHNICAL_SKILL


ALTER TABLE candidate ADD CONSTRAINT FK_candidate_address FOREIGN KEY (address) REFERENCES address (id);

SELECT TABLE_NAME, ENGINE
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = 'works';
ALTER TABLE table_name ENGINE = INNODB;
ALTER TABLE address CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

ALTER TABLE experience MODIFY COLUMN role VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

//truy van cu the 1 candidates 
SELECT 
    c.id AS candidate_id,
    c.full_name,
    c.email,
    c.phone,
    c.dob,
    addr.street,
    addr.city,
    addr.country,
    addr.number,
    addr.zipcode,
    exp.company_name,
    exp.from_date,
    exp.to_date,
    exp.role AS experience_role,
    exp.work_description AS experience_description,
    s.skill_name,
    s.skill_description,
    sl.name AS skill_level
FROM 
    candidate c
LEFT JOIN 
    address addr ON c.address = addr.id
LEFT JOIN 
    experience exp ON c.id = exp.candidate_id
LEFT JOIN 
    candidate_skill cs ON c.id = cs.candidate_id
LEFT JOIN 
    skill s ON cs.skill_id = s.skill_id
LEFT JOIN 
    skill_level sl ON cs.skill_level_id = sl.id
WHERE 
    c.id = 1;

//truy van jobs 
SELECT 
    j.job_id AS job_id,
    j.job_name AS job_name,
    j.job_desc AS job_description,
    c.comp_name AS company_name,
    c.email AS company_email,
    c.phone AS company_phone,
    c.web_url AS company_website,
    addr.street AS company_street,
    addr.city AS company_city,
    addr.country AS company_country,
    addr.zipcode AS company_zipcode,
    s.skill_name AS required_skill,
    s.skill_description AS skill_description,
    sl.name AS skill_level,
    js.more_infos AS skill_more_info
FROM 
    job j
LEFT JOIN 
    company c ON j.company = c.comp_id
LEFT JOIN 
    address addr ON c.address = addr.id
LEFT JOIN 
    job_skill js ON j.job_id = js.job_id
LEFT JOIN 
    skill s ON js.skill_id = s.skill_id
LEFT JOIN 
    skill_level sl ON js.skill_level_id = sl.id
WHERE 
    j.job_id = 1;

//kiem tra chuc nang danh gia ung vien 
SELECT 
    cs.candidate_id, 
    cs.skill_id, 
    sl.name AS skill_level, 
    s.skill_name
FROM candidate_skill cs
JOIN skill_level sl ON cs.skill_level_id = sl.id
JOIN skill s ON cs.skill_id = s.skill_id
WHERE cs.candidate_id = 1;


SELECT 
    js.job_id, 
    js.skill_id, 
    sl.name AS required_skill_level, 
    s.skill_name
FROM job_skill js
JOIN skill_level sl ON js.skill_level_id = sl.id
JOIN skill s ON js.skill_id = s.skill_id
WHERE js.job_id = 1;
UPDATE candidate_skill
SET skill_level_id = 1
WHERE candidate_id = 1 AND skill_id = 1;
SELECT cs.candidate_id, cs.skill_id, sl.name AS skill_level, s.skill_name
FROM candidate_skill cs
JOIN skill_level sl ON cs.skill_level_id = sl.id
JOIN skill s ON cs.skill_id = s.skill_id
WHERE cs.candidate_id = 1;


SELECT cs.candidate_id, cs.skill_id, s.skill_name
FROM candidate_skill cs
JOIN skill s ON cs.skill_id = s.skill_id
WHERE s.skill_name = 'Teamwork';

SELECT js.job_id, js.skill_id, s.skill_name
FROM job_skill js
JOIN skill s ON js.skill_id = s.skill_id
WHERE s.skill_name = 'Teamwork';

SELECT cs.candidate_id, cs.skill_id, sl.name AS candidate_skill_level
FROM candidate_skill cs
JOIN skill_level sl ON cs.skill_level_id = sl.id;

SELECT js.job_id, js.skill_id, sl.name AS job_skill_level
FROM job_skill js
JOIN skill_level sl ON js.skill_level_id = sl.id;

SELECT * FROM candidate_skill WHERE candidate_id = 1;


kiem tra skill 

ALTER TABLE candidate_skill DROP COLUMN skill_level;
ALTER TABLE candidate_skill
ADD COLUMN skill_level ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'PROFESSIONAL', 'EXPERT') NOT NULL;
UPDATE candidate_skill
SET skill_level = 'EXPERT'
WHERE candidate_id = 1;

SELECT 
    cs.candidate_id,
    cs.skill_id AS candidate_skill_id,
    cs.skill_level_id AS candidate_skill_level,
    js.job_id,
    js.skill_id AS job_skill_id,
    js.skill_level_id AS job_skill_level,
    s.skill_name
FROM 
    candidate_skill cs
JOIN 
    job_skill js ON cs.skill_id = js.skill_id
JOIN 
    skill s ON cs.skill_id = s.id
WHERE 
    cs.candidate_id = 1
    AND js.job_id = 1
    AND cs.skill_level_id < js.skill_level_id;
// test thong tin du cua candidates
//them kinh nghiem cho candidate01
INSERT INTO experience (from_date, to_date, role, work_description, company_name, candidate_id)
VALUES 
('2020-01-01', '2023-12-31', 'Software Engineer', 'Developed and maintained web applications.', 'Tech Company A', 1),
('2021-05-01', '2023-12-31', 'Project Manager', 'Managed software development projects.', 'Tech Company B', 1);
//kiem tra xem co ngay sinh khong 
SELECT dob FROM candidate WHERE id = 1;

SELECT * FROM experience WHERE candidate_id = 1;

//test chuc nang goi y cong viec 
-- Thêm kỹ năng vào bảng skill 
INSERT INTO skill (skill_name, skill_description, skill_type_id) VALUES 
('Java', 'Proficient in Java programming', 3),
('Spring Boot', 'Experience in Spring Boot framework', 3),
('ReactJS', 'Frontend development using ReactJS', 3),
('Docker', 'Containerization using Docker', 3);

SHOW CREATE TABLE candidate_skill;

INSERT INTO candidate_skill 
(more_infos, candidate_id, skill_id, skill_level_id, can_id, type, skill_level) 
VALUES 
('Experienced in Java development', 1, (SELECT skill_id FROM skill WHERE skill_name = 'Java' LIMIT 1), 5, 1, 'TECHNICAL_SKILL', 'EXPERT'),
('Built several Spring Boot applications', 1, (SELECT skill_id FROM skill WHERE skill_name = 'Spring Boot' LIMIT 1), 4, 1, 'TECHNICAL_SKILL', 'PROFESSIONAL'),
('Developed responsive web apps with ReactJS', 1, (SELECT skill_id FROM skill WHERE skill_name = 'ReactJS' LIMIT 1), 4, 1, 'TECHNICAL_SKILL', 'PROFESSIONAL'),
('Worked on Dockerized applications', 1, (SELECT skill_id FROM skill WHERE skill_name = 'Docker' LIMIT 1), 3, 1, 'TECHNICAL_SKILL', 'ADVANCED');

INSERT INTO job_skill 
(more_infos, job_id, skill_id, skill_level_id) 
VALUES
('Requires expertise in Java development', 1, (SELECT skill_id FROM skill WHERE skill_name = 'Java' LIMIT 1), 5),
('Knowledge of Spring Boot framework is mandatory', 1, (SELECT skill_id FROM skill WHERE skill_name = 'Spring Boot' LIMIT 1), 4),
('Proficiency in ReactJS for frontend development', 1, (SELECT skill_id FROM skill WHERE skill_name = 'ReactJS' LIMIT 1), 4),
('Experience with Dockerized applications', 1, (SELECT skill_id FROM skill WHERE skill_name = 'Docker' LIMIT 1), 3);
SELECT 
    cs.candidate_id,
    js.job_id,
    s.skill_name,
    'MATCH' AS skill_match
FROM 
    candidate_skill cs
JOIN 
    job_skill js ON cs.skill_id = js.skill_id
JOIN 
    skill s ON cs.skill_id = s.skill_id
WHERE 
    cs.candidate_id = 1 AND js.job_id = 1;

ALTER TABLE job ADD COLUMN job_skills TEXT;
UPDATE job j
LEFT JOIN (
    SELECT js.job_id, GROUP_CONCAT(s.skill_name) AS skills
    FROM job_skill js
    LEFT JOIN skill s ON js.skill_id = s.skill_id
    GROUP BY js.job_id
) skill_data ON j.job_id = skill_data.job_id
SET j.job_skills = skill_data.skills;

UPDATE job j
LEFT JOIN (
    SELECT js.job_id, GROUP_CONCAT(s.skill_name) AS skills
    FROM job_skill js
    LEFT JOIN skill s ON js.skill_id = s.skill_id
    GROUP BY js.job_id
) skill_data ON j.job_id = skill_data.job_id
SET j.job_skills = skill_data.skills;
//kiem tra acccoun 
SELECT a.username, a.role_id, r.role_name 
FROM account a
LEFT JOIN role r ON a.role_id = r.role_id;

SELECT * FROM account WHERE username = 'recruiter1';


ALTER TABLE candidate ADD candidate_skill TEXT;

//chuc nang danh gia ung vien 
SELECT c.id AS candidate_id, c.full_name, c.email, GROUP_CONCAT(cs.skill_id SEPARATOR ', ') AS candidate_skills
FROM candidate c
JOIN candidate_skill cs ON c.id = cs.candidate_id
GROUP BY c.id, c.full_name, c.email;

SELECT 
        j.job_id, 
        j.job_name, 
        GROUP_CONCAT(js.skill_id, ':', js.skill_level SEPARATOR ',') AS job_skills
    FROM job j
    JOIN job_skill js ON j.job_id = js.job_id
    GROUP BY j.job_id, j.job_name;


SELECT 
        c.id AS candidate_id, 
        c.full_name, addresss
        c.email AS candidate_email,
        GROUP_CONCAT(cs.skill_id, ':', cs.skill_level SEPARATOR ',') AS candidate_skills
    FROM candidate c
    JOIN candidate_skill cs ON c.id = cs.candidate_id
    GROUP BY c.id, c.full_name, c.email;

//chinh sua thong tin cong ty 
ALTER TABLE skill DROP FOREIGN KEY FK_skill_skill_type;

//chuc nang them cong viec 
ALTER TABLE skill 
MODIFY COLUMN skill_type ENUM('SOFT_SKILL', 'TECHNICAL_SKILL', 'UNSPECIFIC') NOT NULL DEFAULT 'SOFT_SKILL';
SHOW CREATE TABLE skill;
ALTER TABLE skill CONVERT TO CHARACTER SET utf8mb4 COLLATE UTF8MB4_UNICODE_CI;

//dang ky tai khoan nha tuyen dung  
ALTER TABLE company MODIFY com_id BIGINT NOT NULL AUTO_INCREMENT;
DESCRIBE company;
ALTER TABLE candidate DROP COLUMN can_id;
//sua danh gia ung vien 
INSERT INTO candidate_job (candidate_id, job_id)
VALUES 
(10, 1),
(12, 1),
(13, 1),
(14, 1),
(15, 1);
UPDATE candidate 
SET full_name = 'Nguyen Van B', email = 'candidate2_new@example.com', phone = '0123456781' 
WHERE id = 2;

UPDATE candidate 
SET full_name = 'Tran Thi C', email = 'candidate3_new@example.com', phone = '0123456782' 
WHERE id = 3;

UPDATE candidate 
SET full_name = 'Le Van D', email = 'candidate4_new@example.com', phone = '0123456783' 
WHERE id = 4;

UPDATE candidate 
SET full_name = 'Pham Van E', email = 'candidate5_new@example.com', phone = '0123456784' 
WHERE id = 5;

UPDATE candidate 
SET full_name = 'Nguyen Thi F', email = 'candidate10_new@example.com', phone = '0123456785' 
WHERE id = 10;

UPDATE candidate 
SET full_name = 'Tran Van G', email = 'candidate12_new@example.com', phone = '0123456786' 
WHERE id = 12;

UPDATE candidate 
SET full_name = 'Hoang Thi H', email = 'candidate13_new@example.com', phone = '0123456787' 
WHERE id = 13;

UPDATE candidate 
SET full_name = 'Le Thi I', email = 'candidate14_new@example.com', phone = '0123456788' 
WHERE id = 14;

UPDATE candidate 
SET full_name = 'Pham Van K', email = 'candidate15_new@example.com', phone = '0123456789' 
WHERE id = 15;


