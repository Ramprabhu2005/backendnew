package taskmanagementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import taskmanagementsystem.model.Task;
import taskmanagementsystem.model.TaskHistory;
import taskmanagementsystem.model.User;

import java.util.List;

public interface TaskHistoryRepository extends JpaRepository<TaskHistory, Long> {
    List<TaskHistory> findByTaskIdOrderByTimestampDesc(Long taskId);
    List<TaskHistory> findByUserIdOrderByTimestampDesc(Long userId);
    List<TaskHistory> findByUserIdAndTaskIdOrderByTimestampDesc(Long userId, Long taskId);
}
