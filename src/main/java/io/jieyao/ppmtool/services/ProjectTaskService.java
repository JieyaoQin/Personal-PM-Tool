package io.jieyao.ppmtool.services;

import io.jieyao.ppmtool.domain.Backlog;
import io.jieyao.ppmtool.domain.Project;
import io.jieyao.ppmtool.domain.ProjectTask;
import io.jieyao.ppmtool.exceptions.ProjectNotFoundException;
import io.jieyao.ppmtool.repositories.BacklogRepository;
import io.jieyao.ppmtool.repositories.ProjectRepository;
import io.jieyao.ppmtool.repositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class ProjectTaskService {

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectService projectService;

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask, String username) {

            // wish project sequence to be consecutive
            // make sure user can only add task in their own projects
            Backlog backlog = projectService.findProjectByIdentifier(projectIdentifier, username).getBacklog();
            projectTask.setBacklog(backlog);
            Integer projectTaskSeq = backlog.getPTSequence();
            projectTaskSeq++;
            projectTask.setProjectSequence(projectIdentifier + "-" + projectTaskSeq);
            backlog.setPTSequence(projectTaskSeq);
            projectTask.setProjectIdentifier(projectIdentifier);
            // default value for priority
            if (projectTask.getPriority() == null || projectTask.getPriority() == 0) {
                projectTask.setPriority(3);
            }
            // default value for status
            if (projectTask.getStatus() == null || projectTask.getStatus().equals("")) {
                projectTask.setStatus("TO_DO");
            }
            return projectTaskRepository.save(projectTask);

    }

    public Iterable<ProjectTask> findTasksByProjectId(String projectId, String username) {
        // make sure the user only has access to its own tasks
        projectService.findProjectByIdentifier(projectId, username);
        return projectTaskRepository.findByProjectIdentifierOrderByPriority(projectId);
    }

    public ProjectTask findTaskByProjectSequence(String projectId, String sequence, String username) {
        // make sure we are searching on the right backlog
        projectService.findProjectByIdentifier(projectId, username);
        // make sure the task exist
        ProjectTask projectTask = projectTaskRepository.findByProjectSequence(sequence);
        if (projectTask == null) {
            throw new ProjectNotFoundException("Project Task " + sequence + " does not exist");
        }
        // make sure the task with this sequence belongs to project with this id
        if (!projectTask.getProjectIdentifier().equals(projectId)) {
            throw new ProjectNotFoundException("Project Task " + sequence + " does not exist in project " + projectId);
        }
        return projectTask;
    }

    public ProjectTask updateByProjectSequence(ProjectTask updatedTask, String projectId, String sequence, String username) {
        ProjectTask projectTask = findTaskByProjectSequence(projectId, sequence, username);
        return projectTaskRepository.save(updatedTask);
    }

    public void deleteTaskBySequence(String projectId, String taskId, String username) {
        ProjectTask projectTask = findTaskByProjectSequence(projectId, taskId, username);
        projectTaskRepository.delete(projectTask);
    }
}
