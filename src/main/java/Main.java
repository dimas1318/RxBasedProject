import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

public class Main {

    public static void main(String[] args) {
        Executor fiberExecutor = Fiber::schedule;
        Scheduler fiberScheduler = Schedulers.from(fiberExecutor);

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
                    .subscribeOn(fiberScheduler)
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
}
