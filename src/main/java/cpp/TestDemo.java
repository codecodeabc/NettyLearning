package cpp;

import java.util.concurrent.ConcurrentHashMap;

public class TestDemo {
    public static void main(String[] args) throws Exception{
        /*HashBiMap<String, String> map = HashBiMap.create();
        TimeUnit.MILLISECONDS.sleep(10);*/
        /*new Thread(){
            @Override
            public void run() {
                for(int i = 0; i < 100;i++){
                    try {
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String s = map.get(i + "");
                    System.out.println(s);
                }

            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                for(int i = 0; i < 100;i++){
                    try {
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    map.put(i+"",i+100+"");
                }

            }
        }.start();*/

        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        map.put("1",66);
        map.put("1",88);
        System.out.println(map.get("1"));
    }
}
