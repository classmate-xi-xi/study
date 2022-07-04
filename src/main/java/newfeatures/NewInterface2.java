package newfeatures;

public interface NewInterface2 {

    default void sms(){
        System.out.println();
    }

    static void s(){
        System.out.println();
    }

    default void f(){
        System.out.println(1);
    }
}
