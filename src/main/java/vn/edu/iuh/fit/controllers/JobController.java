package vn.edu.iuh.fit.controllers;

import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.iuh.fit.models.Job;
import vn.edu.iuh.fit.services.JobService;
import vn.edu.iuh.fit.services.SkillService;

@Controller
@RequestMapping("/jobs")
public class JobController {
    @Autowired
    private JobService jobService;

    @Autowired
    private SkillService skillService;

    // Hiển thị danh sách việc làm
    @GetMapping
    public String listJobs(Model model) {
        model.addAttribute("jobs", jobService.findAllJobs());
        return "jobs/jobs.html";
    }

    // Trang thêm mới công việc
    @GetMapping("/new")
    public String showNewJobForm(Model model) {
        model.addAttribute("job", new Job());
        model.addAttribute("skills", skillService.findAll());
        return "jobs/new";
    }

    // Xử lý việc thêm mới công việc
    @PostMapping
    public String addJob(@ModelAttribute Job job) {
        jobService.saveJob(job);
        return "redirect:/jobs";
    }
}