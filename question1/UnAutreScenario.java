package question1;

public class UnAutreScenario{
    public static void main(String[] args) throws Exception{
        Consumer consumer=null;
        try{
            Thread prod = new Thread(new Runnable(){
                        public void run(){
                            Provider provider=null;
                            try{
                                provider = new Provider("request","reply");
                            }catch(Exception e){
                            }finally{
                                try{provider.close();}catch(Exception e){}
                            }
                        }});

            consumer = new Consumer("request","reply");
            consumer.send("test_envoi");
            prod.start();
            System.out.print("message recu : ");
            System.out.println(consumer.receive().toString());
            //Thread.sleep(1000000);
        }finally{
            consumer.close();
        }
    }
}
