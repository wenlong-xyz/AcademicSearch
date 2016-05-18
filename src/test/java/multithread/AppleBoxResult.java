package multithread;

/** 
 * 公共资源苹果箱子类 
 */  
public class AppleBoxResult {  
    private int appleNum = 0;  
  
    /** 
     * 增加苹果 
     */  
    public synchronized void increace() {  
        appleNum++;  
        System.out.println("增加后的Result数量：" + appleNum);  
        notify();  
    }  
  
    /** 
     * 减少苹果 
     */  
    public synchronized void decreace() {  
        while (appleNum == 0) {  
            try {  
                wait();  
            } catch (InterruptedException e) {  
                e.printStackTrace();  
            }  
        }  
        appleNum--;  
        System.out.println("减少后的Result数量：" + appleNum);  
        notify();  
    }  
}  