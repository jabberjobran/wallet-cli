@Slf4j
public class Configuration {

    private static volatile Config config; // volatile برای ایمنی در محیط چندنخی

    public static Config getByPath(final String configurationPath) {
        if (isBlank(configurationPath)) {
            throw new IllegalArgumentException("Configuration path is required!");
        }

        if (config == null) {
            synchronized (Configuration.class) {
                if (config == null) {
                    config = loadConfig(configurationPath);
                }
            }
        }
        return config;
    }

    private static Config loadConfig(String path) {
        File externalFile = new File(System.getProperty("user.dir"), path);
        if (externalFile.exists()) {
            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(externalFile))) {
                logger.info("بارگذاری پیکربندی از فایل خارجی: {}", externalFile.getAbsolutePath());
                return ConfigFactory.parseReader(reader);
            } catch (IOException e) {
                logger.warn("خواندن فایل پیکربندی خارجی ناموفق بود، بازگشت به classpath: {}", e.getMessage());
            }
        } else {
            logger.debug("فایل پیکربندی خارجی یافت نشد: {}", externalFile.getAbsolutePath());
        }

        logger.info("بارگذاری پیکربندی از classpath: {}", path);
        return ConfigFactory.load(path);
    }
}
