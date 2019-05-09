import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

public class Main {

    public static void main(String[] args) {
//        System.out.println("HEllo yopta");

//        Observable<String> simpleObservable = Observable.just("HEllo yopta");
//        simpleObservable.subscribe(new DisposableObserver<String>() {
//
//            public void onNext(String s) {
//                System.out.println(s);
//            }
//
//            public void onError(Throwable throwable) {
//
//            }
//
//            public void onComplete() {
//
//            }
//        });

//        Fiber.schedule(new Runnable() {
//            public void run() {
//                System.out.println("start");
//                try {
//                    Thread.sleep(15000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                System.out.println("Hello koko");
//            }
//        }).awaitTermination();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("Hello koko");
//            }
//        }).start();

        Executor executor = Fiber::schedule;
//        ExecutorService service = new ForkJoinPool(){
//            @Override
//            public void execute(Runnable task) {
////                super.execute(task);
//                Fiber.schedule(task);
//            }
//        };
        Scheduler scheduler = Schedulers.from(executor);

        int n = 20;

        final CountDownLatch latch = new CountDownLatch(n);

        long start = System.currentTimeMillis();

        for (int i = 0; i < n; i++) {
            Completable
                    .fromRunnable(() -> {
                        try {
                            URL url = new URL("https://i.pinimg.com/736x/c7/c7/56/c7c756b0221cc82b2a3ed9aa0c8e6f02.jpg");
                            BufferedImage image = ImageIO.read(url);
                            ImageIO.write(image, "jpg", getUniqueFilePath("parent", "child", "imag.jpg"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    })
//                    .subscribeOn(Schedulers.io())
//                    .subscribeOn(Schedulers.computation())
                    .subscribeOn(scheduler)
                    .subscribe(new CompletableObserver() {

                        @Override
                        public void onSubscribe(Disposable disposable) {

                        }

                        @Override
                        public void onComplete() {
                            latch.countDown();
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            latch.countDown();
                        }
                    });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long stop = System.currentTimeMillis();
        System.out.println("It's " + (stop - start));
    }

    public static File getUniqueFilePath(String parent, String child, String fileName) {
        File dir = new File(parent, child);
        String uniqueName = getUniqueFileName(parent, child, fileName);
        return new File(dir, uniqueName);
    }

    public synchronized static String getUniqueFileName(String parent, String child, String fileName) {
        final File dir = new File(parent, child);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        int num = 0;
        final String ext = getFileExtension(fileName);
        final String name = getFileName(fileName);
        File file = new File(dir, fileName);
        while (file.exists()) {
            num++;
            file = new File(dir, name + "-" + num + ext);
        }
        return file.getName();
    }

    public static String getFileExtension(final String path) {
        if (path != null && path.lastIndexOf('.') != -1) {
            return path.substring(path.lastIndexOf('.'));
        }
        return null;
    }


    public static String getFileName(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    private static <T extends Serializable> byte[] pickle(T obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.close();
        return baos.toByteArray();
    }

    private static <T extends Serializable> T unpickle(byte[] b, Class<T> cl) throws
            IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object o = ois.readObject();
        return cl.cast(o);
    }
}
