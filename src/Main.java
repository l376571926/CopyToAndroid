import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("需将视频路径做为参数传入，示例：java -jar WindowsCommand.jar H:\\ztydhy");
            return;
        }
        System.out.println("传入参数：" + Arrays.toString(args));
        String dirPath = args[0];//传入的文件夹路径
        String parentDirName = dirPath.substring(dirPath.lastIndexOf("\\") + 1);//文件夹名
        File file = new File(dirPath);
        List<String> filePathList = new ArrayList<>();
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File aviFile : files) {//H:\ztydhy\[迅雷仓XunLeiCang.Com]太阳的后裔02.720p.mp4
                    filePathList.add(aviFile.getPath());
                }
            }
        }

        //获取Windows命令运行实例
        Runtime runtime = Runtime.getRuntime();
        if (!filePathList.isEmpty()) {
            String mp4DirPath = "/sdcard/" + parentDirName;
            //在sd卡根目录创建同名文件夹
            String mkdirCommand = "adb shell mkdir -p " + mp4DirPath;
            runtime.exec(mkdirCommand);
            for (String filePath : filePathList) {
                int start = filePath.lastIndexOf("\\") + 1;
                String fileName = filePath.substring(start);
                if (fileName.contains(" ")) {
                    System.out.println("文件名不能包含空格，跳过不处理：" + fileName);
                    continue;
                }
                //循环推送文件到手机中
                String pushCommand = "adb push" + " " + filePath + " " + mp4DirPath + "/" + fileName;
                Process process1 = runtime.exec(pushCommand);
                printCommandResult(process1);
            }
        }
    }

    private static void printCommandResult(Process process) throws IOException {
        InputStream inputStream = process.getInputStream();
        //这里一定要指定编码格式，否则在cmd.exe中会输出乱码
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line = "";
        while (line != null) {
            line = bufferedReader.readLine();
            if (line != null) {
                System.out.println(line);
            }
        }
        //关闭流
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();
    }

}
