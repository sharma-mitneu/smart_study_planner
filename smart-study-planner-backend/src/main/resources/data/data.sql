-- File: src/main/resources/data.sql

-- Insert demo user (password: password)
INSERT INTO users (id, username, password, email, role, created_at)
VALUES (1, 'demouser', '$2a$10$ywF8QBmzsDeYxiO3aBE5iOyQF2U5D0w1bH6nJSN8lW5UQxGQfpToO', 'demo@example.com', 'STUDENT', CURRENT_TIMESTAMP());

-- Insert subjects
INSERT INTO subjects (id, name, description, priority, user_id, created_at)
VALUES
(1, 'Mathematics', 'Advanced calculus and linear algebra', 'HIGH', 1, CURRENT_TIMESTAMP()),
(2, 'Computer Science', 'Java programming and algorithms', 'MEDIUM', 1, CURRENT_TIMESTAMP()),
(3, 'Physics', 'Mechanics and thermodynamics', 'MEDIUM', 1, CURRENT_TIMESTAMP()),
(4, 'History', 'World History 1900-2000', 'LOW', 1, CURRENT_TIMESTAMP());

-- Insert tasks
INSERT INTO tasks (id, title, description, due_date, completed, priority, subject_id, user_id, recurring, created_at)
VALUES
-- Math Tasks
(1, 'Complete Calculus Assignment', 'Problems 1-20 from Chapter 5', DATEADD('DAY', 3, CURRENT_DATE()), false, 'HIGH', 1, 1, false, CURRENT_TIMESTAMP()),
(2, 'Study for Math Quiz', 'Review differentiation rules and integration techniques', DATEADD('DAY', 7, CURRENT_DATE()), false, 'HIGH', 1, 1, false, CURRENT_TIMESTAMP()),
(3, 'Watch Linear Algebra Lecture', 'Video lecture on eigenvalues and eigenvectors', DATEADD('DAY', 1, CURRENT_DATE()), false, 'MEDIUM', 1, 1, false, CURRENT_TIMESTAMP()),

-- CS Tasks
(4, 'Java Project', 'Complete the implementation of data structures', DATEADD('DAY', 14, CURRENT_DATE()), false, 'HIGH', 2, 1, false, CURRENT_TIMESTAMP()),
(5, 'Algorithm Analysis', 'Big O notation practice problems', DATEADD('DAY', 4, CURRENT_DATE()), false, 'MEDIUM', 2, 1, false, CURRENT_TIMESTAMP()),
(6, 'Read Chapter on Recursion', 'Textbook pages 120-150', DATEADD('DAY', 2, CURRENT_DATE()), false, 'LOW', 2, 1, false, CURRENT_TIMESTAMP()),

-- Physics Tasks
(7, 'Physics Lab Report', 'Write up results from pendulum experiment', DATEADD('DAY', 5, CURRENT_DATE()), false, 'HIGH', 3, 1, false, CURRENT_TIMESTAMP()),
(8, 'Practice Problems', 'End of chapter problems 15-30', DATEADD('DAY', 6, CURRENT_DATE()), false, 'MEDIUM', 3, 1, false, CURRENT_TIMESTAMP()),

-- History Tasks
(9, 'Read about World War II', 'Textbook chapter on major events', DATEADD('DAY', 3, CURRENT_DATE()), false, 'MEDIUM', 4, 1, false, CURRENT_TIMESTAMP()),
(10, 'Start Research for Essay', 'Find sources for Cold War impact essay', DATEADD('DAY', 8, CURRENT_DATE()), false, 'LOW', 4, 1, false, CURRENT_TIMESTAMP());

-- Insert some progress records
INSERT INTO progress (id, date, minutes_spent, notes, task_id, user_id, created_at)
VALUES
-- Math progress
(1, CURRENT_DATE(), 45, 'Completed first 10 problems', 1, 1, CURRENT_TIMESTAMP()),
(2, DATEADD('DAY', -1, CURRENT_DATE()), 60, 'Reviewed differentiation rules', 2, 1, CURRENT_TIMESTAMP()),

-- CS progress
(3, DATEADD('DAY', -2, CURRENT_DATE()), 90, 'Implemented linked list and binary search tree', 4, 1, CURRENT_TIMESTAMP()),
(4, CURRENT_DATE(), 30, 'Read about time complexity', 5, 1, CURRENT_TIMESTAMP()),

-- Physics progress
(5, DATEADD('DAY', -1, CURRENT_DATE()), 60, 'Conducted experiment and collected data', 7, 1, CURRENT_TIMESTAMP()),

-- History progress
(6, DATEADD('DAY', -3, CURRENT_DATE()), 45, 'Read about the beginning of WWII', 9, 1, CURRENT_TIMESTAMP());