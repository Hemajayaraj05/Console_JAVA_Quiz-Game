import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

class Questions{
    String question;
    String[] options;
    int correctAns;

    public Questions(String question,String[] options,int correctAns){
        this.question=question;
        this.options=options;
        this.correctAns=correctAns;
    }
    public boolean isCorrect(int answer)
    {
        return answer==correctAns;
    }
}


public class QuizGame{
    private ArrayList<Questions> questions=new ArrayList<>();
    private int score=0;
    public Scanner s=new Scanner(System.in);

    private Connection connection;
    public QuizGame(){
        connectToDB();
        loadQuestionsfromDB();
    }

    private void connectToDB(){
        try 
        {
            connection=DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "Hemaj@02");
            System.out.println("Connected to PostgreSQL database successfully!");
        } 
        catch(SQLException e) 
        {
            e.printStackTrace();
        }
    }

    private void loadQuestionsfromDB(){
        String query="SELECT question_text,option1,option2,option3,option4,correct_answer FROM questions";
        try (Statement stmt=connection.createStatement(); ResultSet rs=stmt.executeQuery(query)) 
        {
            while(rs.next()) 
            {
                String questionText=rs.getString("question_text");
                String[] options={
                    rs.getString("option1"),
                    rs.getString("option2"),
                    rs.getString("option3"),
                    rs.getString("option4")
                };
                int correctAns=rs.getInt("correct_answer");

                questions.add(new Questions(questionText, options, correctAns));
            }

            System.out.println("Questions loaded from PostgreSQL successfully!");
        } 
        
        catch(SQLException e) 
        {
            e.printStackTrace();
        }
        
    }

     public void addQuestions(){
        System.out.println("Enter the Question:");
        String questionText=s.nextLine();
        String[] options=new String[4];
        for(int i=0;i<4;i++)
        {
            System.out.println("Enter option "+(i+1)+":");
            options[i]=s.nextLine();
        }
        System.out.println("Enter the correct option Number (1-4):");
        int correctAns=s.nextInt();
        s.nextLine();
        String insertQuery="INSERT INTO questions(question_text,option1,option2,option3,option4,correct_answer) VALUES(?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt=connection.prepareStatement(insertQuery)) {
            pstmt.setString(1, questionText);
            pstmt.setString(2, options[0]);
            pstmt.setString(3, options[1]);
            pstmt.setString(4, options[2]);
            pstmt.setString(5, options[3]);
            pstmt.setInt(6, correctAns);
            pstmt.executeUpdate();
            System.out.println("Question added to PostgreSQL successfully!");
        } 
        catch(SQLException e) 
        {
            e.printStackTrace();
        }

     }



     public void startQuiz(){
        System.out.println("Welcome to the Quiz Game!");
        System.out.println("Answer by typiing the number of your choice.\n");

        for(Questions q:questions)
        {
            System.out.println(q.question);
            for(int i=0;i<q.options.length;i++)
            {
                System.out.println((i+1)+")"+q.options[i]);
            }
        

        System.out.println("Your Answer: ");
        int answer=s.nextInt();
        if(q.isCorrect(answer))
        {
              System.out.println("Hurray! Your Answer is Correct!");
              score++;
        }
        else{
            System.out.println("Oops!Your Answer is Wrong :(");
        }
        System.out.println();
    }
          displayScore();
     }


     public void displayScore(){
        System.out.println("Quiz Finished!");
        System.out.println("Your Score: "+score+"/"+questions.size());

        if(score==questions.size())
        {
            System.out.println("You got all answers correct!");
        }
        else if(score>questions.size()/2)
        {
            System.out.println("You scored above average!");
        }
        else{
            System.out.println("Better luck next time!");
        }
     }

     public static void main(String[] args)
     {
        QuizGame quizGame=new QuizGame();
       
        while(true)
        {
            System.out.println("\nChoose an option:");
            System.out.println("1. Start Quiz");
            System.out.println("2. Add a Question");
            System.out.println("3. Exit");

            int choice=quizGame.s.nextInt();
            quizGame.s.nextLine();
            switch(choice){
                case 1:
                quizGame.startQuiz();
                break;
                case 2:
                quizGame.addQuestions();
                break;
                case 3:
                System.out.println("Exiting the Game");
                return ;
                default:
                System.out.println("Invalid Choice");
            }

         
        }

     }

}