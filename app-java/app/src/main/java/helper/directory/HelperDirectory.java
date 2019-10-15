package helper.directory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import main.G;

public class HelperDirectory {
  private static HelperDirectory instance;
  public HelperDirectory() {}
  public static HelperDirectory getInstance(){
    if(instance == null){
      synchronized (HelperDirectory.class) {
        if(instance == null){
          instance = new HelperDirectory();
        }
      }
    }
    return instance;
  }
  public  void copyFile(String inputFilename, String outputFilename) {
    InputStream inputStream = null;
    OutputStream outputStream = null;
    try {
      inputStream = new FileInputStream(inputFilename);
      outputStream = new FileOutputStream(outputFilename);

      copyFile(inputStream, outputStream);
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
    } finally {
      closeStream(inputStream);
      closeStream(outputStream);
    }
  }
  private static void copyFile(InputStream inputStream, OutputStream outputStream) {
    byte[] buffer = new byte[8 * 1024];
    int lenRead = 0;
    try {
      while ((lenRead = inputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, lenRead);
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    } finally {
      closeStream(inputStream);
      closeStream(outputStream);
    }
  }
  private static void closeStream(InputStream stream) {
    try {
      stream.close();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }


  private static void closeStream(OutputStream stream) {
    try {
      stream.flush();
      stream.close();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }
  public void createFullDirApp(onCreateDirListener onCreateDirListener){
    //TODO  create full directory app
    File dbDir = new File(G.DB_DIR);
    if (!dbDir.exists()) {
      if (dbDir.mkdirs()){
        onCreateDirListener.onSucsess();
      }else {
        onCreateDirListener.onError();
      }
    }else{
      onCreateDirListener.onSucsess();
    }
  }
}
