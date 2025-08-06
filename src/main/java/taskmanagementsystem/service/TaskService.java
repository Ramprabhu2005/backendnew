package taskmanagementsystem.service;

import taskmanagementsystem.dto.ApiResponse;
import taskmanagementsystem.model.Task;

import java.util.List;

public interface TaskService {
    ApiResponse createTask(Task task, Long userId);

    ApiResponse getTaskById(Integer taskId);

    List<Task> getAllTasks(Long userId);

    ApiResponse updateTask(Task task, Integer id);

    void deleteTask(Integer id);

    /**
     * @deprecated Use markTaskAsCompleted instead
     */
    @Deprecated
    ApiResponse doneTask(Integer id);
    
    /**
     * Marks a task as completed and logs the action
     * @param id The ID of the task to mark as completed
     * @return ApiResponse with the updated task
     */
    ApiResponse markTaskAsCompleted(Integer id);

    /**
     * Marks a task as pending (not completed) and logs the action
     * @param id The ID of the task to mark as pending
     * @return ApiResponse with the updated task
     */
    ApiResponse markTaskAsPending(Integer id);
    
    /**
     * @deprecated Use markTaskAsPending instead
     */
    @Deprecated
    ApiResponse pendingTask(Integer id);
}