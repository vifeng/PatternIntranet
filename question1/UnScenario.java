package question1;

public class UnScenario{
    public static void main(String[] args) throws Exception{
        Provider provider=null;
        Consumer consumer=null;
        try{
            provider = new Provider("request","reply");
            consumer = new Consumer("request","reply");
            consumer.send("test_envoi");
            System.out.print("message recu : ");
            System.out.println(consumer.receive().toString());
            //Thread.sleep(1000000);
        }finally{
            consumer.close();
            provider.close();
        }
    }
}
