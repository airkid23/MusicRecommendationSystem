import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @program: MusicRecommendationSystem
 * @package: PACKAGE_NAME
 * @description:
 * @author: wzj
 * @create: 2020-09-25 10:11
 **/

public class testUtil {
    public static void main(String[] args) {
        String ts = "1536153503759";
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

        String date = format.format(new Date(Long.valueOf(ts) ));
        System.out.println(date);
    }
}
