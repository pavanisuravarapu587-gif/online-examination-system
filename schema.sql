CREATE DATABASE IF NOT EXISTS examdb;
USE examdb;

CREATE TABLE IF NOT EXISTS question (
    id      INT AUTO_INCREMENT PRIMARY KEY,
    text    VARCHAR(300) NOT NULL,
    opt1    VARCHAR(100) NOT NULL,
    opt2    VARCHAR(100) NOT NULL,
    opt3    VARCHAR(100) NOT NULL,
    opt4    VARCHAR(100) NOT NULL,
    correct INT NOT NULL            -- 1..4
);

CREATE TABLE IF NOT EXISTS result (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    student  VARCHAR(50) NOT NULL,
    score    INT NOT NULL,
    total    INT NOT NULL,
    taken_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO question(text,opt1,opt2,opt3,opt4,correct) VALUES
('Which AWT class is the top-level window with a title bar and menu bar?','Panel','Frame','Canvas','Label',2),
('Which layout manager shows only one component at a time?','FlowLayout','GridLayout','CardLayout','BorderLayout',3),
('Which JDBC driver type is written purely in Java?','Type 1','Type 2','Type 3','Type 4',4),
('Which method is called only once in a servlet life cycle?','doGet()','service()','init()','doPost()',3),
('A JSP page is translated into a:','Applet','Servlet','Frame','Class file only',2),
('Which interface handles mouse button clicks in AWT?','KeyListener','MouseListener','ItemListener','TextListener',2);
