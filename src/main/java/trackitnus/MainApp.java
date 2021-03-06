package trackitnus;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.stage.Stage;
import trackitnus.commons.core.Config;
import trackitnus.commons.core.LogsCenter;
import trackitnus.commons.core.Version;
import trackitnus.commons.exceptions.DataConversionException;
import trackitnus.commons.util.ConfigUtil;
import trackitnus.commons.util.StringUtil;
import trackitnus.logic.Logic;
import trackitnus.logic.LogicManager;
import trackitnus.model.Model;
import trackitnus.model.ModelManager;
import trackitnus.model.ReadOnlyTrackIter;
import trackitnus.model.ReadOnlyUserPrefs;
import trackitnus.model.TrackIter;
import trackitnus.model.UserPrefs;
import trackitnus.model.util.SampleDataUtil;
import trackitnus.storage.JsonTrackIterStorage;
import trackitnus.storage.JsonUserPrefsStorage;
import trackitnus.storage.Storage;
import trackitnus.storage.StorageManager;
import trackitnus.storage.TrackIterStorage;
import trackitnus.storage.UserPrefsStorage;
import trackitnus.ui.Ui;
import trackitnus.ui.UiManager;

/**
 * Runs the application.
 */
public class MainApp extends Application {

    public static final Version VERSION = new Version(1, 4, 0, false);

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);

    protected Ui ui;
    protected Logic logic;
    protected Storage storage;
    protected Model model;
    protected Config config;

    @Override
    public void init() throws Exception {
        logger.info("=============================[ Initializing TrackIter ]===========================");
        super.init();

        AppParameters appParameters = AppParameters.parse(getParameters());
        config = initConfig(appParameters.getConfigPath());

        UserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(config.getUserPrefsFilePath());
        UserPrefs userPrefs = initPrefs(userPrefsStorage);
        TrackIterStorage trackIterStorage = new JsonTrackIterStorage(userPrefs.getTrackIterFilePath());
        storage = new StorageManager(trackIterStorage, userPrefsStorage);

        initLogging(config);

        model = initModelManager(storage, userPrefs);

        logic = new LogicManager(model, storage);

        ui = new UiManager(logic);
    }

    /**
     * Returns a {@code ModelManager} with the data from {@code storage}'s TrackIter and {@code userPrefs}. <br>
     * The sample data will be used instead if {@code storage}'s TrackIter is not found,
     * or an empty TrackIter will be used instead if errors occur when reading {@code storage}'s TrackIter.
     */
    private Model initModelManager(Storage storage, ReadOnlyUserPrefs userPrefs) {
        Optional<ReadOnlyTrackIter> trackIterOptional;
        ReadOnlyTrackIter initialData;
        try {
            trackIterOptional = storage.readTrackIter();
            if (trackIterOptional.isEmpty()) {
                logger.info("Data file not found. Will be starting with a sample TrackIter");
            }
            initialData = trackIterOptional.orElseGet(SampleDataUtil::getSampleTrackIter);
        } catch (DataConversionException e) {
            logger.warning("Data file not in the correct format. Will be starting with an empty TrackIter");
            initialData = new TrackIter();
        } catch (IOException e) {
            logger.warning("Problem while reading from the file. Will be starting with an empty TrackIter");
            initialData = new TrackIter();
        }

        return new ModelManager(initialData, userPrefs);
    }

    private void initLogging(Config config) {
        LogsCenter.init(config);
    }

    /**
     * Returns a {@code Config} using the file at {@code configFilePath}. <br>
     * The default file path {@code Config#DEFAULT_CONFIG_FILE} will be used instead
     * if {@code configFilePath} is null.
     */
    protected Config initConfig(Path configFilePath) {
        Config initializedConfig;
        Path configFilePathUsed;

        configFilePathUsed = Config.DEFAULT_CONFIG_FILE;

        if (configFilePath != null) {
            logger.info("Custom Config file specified " + configFilePath);
            configFilePathUsed = configFilePath;
        }

        logger.info("Using config file : " + configFilePathUsed);

        try {
            Optional<Config> configOptional = ConfigUtil.readConfig(configFilePathUsed);
            initializedConfig = configOptional.orElse(new Config());
        } catch (DataConversionException e) {
            logger.warning("Config file at " + configFilePathUsed + " is not in the correct format. "
                + "Using default config properties");
            initializedConfig = new Config();
        }

        //Update config file in case it was missing to begin with or there are new/unused fields
        try {
            ConfigUtil.saveConfig(initializedConfig, configFilePathUsed);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }
        return initializedConfig;
    }

    /**
     * Returns a {@code UserPrefs} using the file at {@code storage}'s user prefs file path,
     * or a new {@code UserPrefs} with default configuration if errors occur when
     * reading from the file.
     */
    protected UserPrefs initPrefs(UserPrefsStorage storage) {
        Path prefsFilePath = storage.getUserPrefsFilePath();
        logger.info("Using prefs file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataConversionException e) {
            logger.warning("UserPrefs file at " + prefsFilePath + " is not in the correct format. "
                + "Using default user prefs");
            initializedPrefs = new UserPrefs();
        } catch (IOException e) {
            logger.warning("Problem while reading from the file. Will be starting with an empty TrackIter");
            initializedPrefs = new UserPrefs();
        }

        //Update prefs file in case it was missing to begin with or there are new/unused fields
        try {
            storage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }

        return initializedPrefs;
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting TrackIter " + MainApp.VERSION);
        ui.start(primaryStage);
    }

    @Override
    public void stop() {
        logger.info("============================ [ Stopping TrackIter ] =============================");
        try {
            storage.saveUserPrefs(model.getUserPrefs());
        } catch (IOException e) {
            logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
        }
    }
}
