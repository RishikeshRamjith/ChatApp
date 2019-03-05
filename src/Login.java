import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Login{
    static ArrayList<User> ListOfUsers = new ArrayList<User>();
    static BufferedReader br = null;

    public static void RegisterNewUser(String name, String password){
        ListOfUsers.add(new User(name,password));
        BufferedWriter bw = null;
        try{
            bw = new BufferedWriter(new FileWriter("db.txt",true));
            String line = null;
            bw.write(name+"\n");
            bw.write(password+"\n");
            bw.write("\n");
            bw.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }

    }

    public static boolean UsernameTaken(String username){
        for(int i = 0; i < ListOfUsers.size(); i++){
            if(username.equals(ListOfUsers.get(i).getName())){
                return true;
            }
            else{
                continue;
            }
        }
        return false;
    }

    public static boolean ValidateLogin(String name, String password){
        for(int i = 0; i < ListOfUsers.size(); i++){
            if(name.equals(ListOfUsers.get(i).getName()) && password.equals(ListOfUsers.get(i).getPassword())){
                return true;
            }
            else{
                continue;
            }
        }
        return false;
    }

    public static void loadUsers(){
        try{
            br = new BufferedReader(new FileReader("db.txt"));
            String line = null;
            int count = 0;
            String name = null;
            String password = null;

            while((line = br.readLine()) !=null){
                //System.out.println(line+"***");
                if(count == 0){
                    name = line;
                    //System.out.println(name+"***");
                    count++;
                    continue;
                }
                if(count == 1){
                    password = line;
                    count++;
                    continue;
                    //System.out.println(password+"***");
                }
                if(count == 2){
                    ListOfUsers.add(new User(name,password));
                    count = 0;
                    continue;
                }
            }
            br.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
