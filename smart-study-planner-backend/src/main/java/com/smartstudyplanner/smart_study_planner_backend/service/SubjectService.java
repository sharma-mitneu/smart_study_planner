package com.smartstudyplanner.smart_study_planner_backend.service;


import com.smartstudyplanner.smart_study_planner_backend.model.Subject;
import com.smartstudyplanner.smart_study_planner_backend.model.User;
import com.smartstudyplanner.smart_study_planner_backend.repository.SubjectRepository;
import com.smartstudyplanner.smart_study_planner_backend.repository.UserRepository;
import com.smartstudyplanner.smart_study_planner_backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;

    @Transactional
    public Subject createSubject(Subject subject, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        subject.setUser(user);
        return subjectRepository.save(subject);
    }

    public List<Subject> getUserSubjects(Integer userId) {
        return subjectRepository.findByUserIdOrderByPriorityDesc(userId);
    }

    @Transactional
    public Subject updateSubject(Integer id, Subject subjectDetails) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        subject.setName(subjectDetails.getName());
        subject.setDescription(subjectDetails.getDescription());
        subject.setPriority(subjectDetails.getPriority());

        return subjectRepository.save(subject);
    }

    public void deleteSubject(Integer id) {
        if (!subjectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Subject not found");
        }
        subjectRepository.deleteById(id);
    }
}
