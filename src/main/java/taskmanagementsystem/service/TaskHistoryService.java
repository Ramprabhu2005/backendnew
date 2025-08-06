package taskmanagementsystem.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import taskmanagementsystem.model.Task;
import taskmanagementsystem.model.TaskHistory;
import taskmanagementsystem.model.User;
import taskmanagementsystem.repository.TaskHistoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskHistoryService {

    private final TaskHistoryRepository taskHistoryRepository;
    private final ObjectMapper objectMapper;

    public void logTaskCreation(Task task, User user) {
        logTaskAction(task, user, "CREATED", null);
    }

    public void logTaskUpdate(Task task, User user, String previousState) {
        logTaskAction(task, user, "UPDATED", previousState);
    }

    public void logTaskCompletion(Task task, User user) {
        logTaskAction(task, user, "COMPLETED", null);
    }

    public void logTaskDeletion(Task task, User user, String previousState) {
        logTaskAction(task, user, "DELETED", previousState);
    }

    public List<TaskHistory> getTaskHistory(Long taskId) {
        return taskHistoryRepository.findByTaskIdOrderByTimestampDesc(taskId);
    }

    public List<TaskHistory> getUserTaskHistory(Long userId, Long taskId) {
        return taskHistoryRepository.findByUserIdAndTaskIdOrderByTimestampDesc(userId, taskId);
    }

    private void logTaskAction(Task task, User user, String action, String previousState) {
        try {
            TaskHistory history = new TaskHistory();
            history.setTask(task);
            history.setUser(user);
            history.setAction(action);
            
            // If previous state is not provided, use current task state
            String details = previousState != null ? previousState : convertTaskToJson(task);
            history.setDetails(details);
            
            taskHistoryRepository.save(history);
        } catch (Exception e) {
            // Log error but don't fail the main operation
            e.printStackTrace();
        }
    }

    private String convertTaskToJson(Task task) {
        try {
            return objectMapper.writeValueAsString(task);
        } catch (JsonProcessingException e) {
            return "{\"error\":\"Failed to serialize task\"}";
        }
    }
}
