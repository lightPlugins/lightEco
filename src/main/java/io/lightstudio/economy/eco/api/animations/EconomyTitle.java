package io.lightstudio.economy.eco.api.animations;

import io.lightstudio.economy.Light;
import io.lightstudio.economy.eco.LightEco;
import io.lightstudio.economy.util.NumberFormatter;
import io.lightstudio.economy.util.TitleSender;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
public class EconomyTitle {

    private final Player player;
    private BigDecimal amountToCount;
    private BigDecimal currentAmount = BigDecimal.ZERO;
    private boolean isDeposit;
    private boolean isCompleted = false;
    // title settings coming from settings.yml
    private boolean titleEnable;
    private double titleMinAmount;
    // TODO: Implement title style system LINEAR and EXPONENTIAL
    private String titleStyle;
    private int titleDuration;
    // title sound settings coming from settings.yml
    private boolean soundEnable;
    private Sound soundType;
    private float soundVolume;
    private String soundChannel;
    private float minPitch = 0.1f;
    private float maxPitch = 0.4f;
    // title deposit messages coming from messages.yml
    private String titleDepositFinalTitle;
    private String titleDepositFinalSubtitle;
    private String titleDepositCountTitle;
    private String titleDepositCountSubtitle;
    // title withdraw messages coming from messages.yml
    private String titleWithdrawFinalTitle;
    private String titleWithdrawFinalSubtitle;
    private String titleWithdrawCountTitle;
    private String titleWithdrawCountSubtitle;

    private EconomyTitle(Builder builder) {
        this.player = builder.player;
        this.amountToCount = builder.amountToCount;
        this.isDeposit = builder.isDeposit;
        fetchSettings();
    }

    private void fetchSettings() {

        this.titleEnable = LightEco.getSettingParams().titleSettings().titleEnabled();
        this.titleMinAmount = LightEco.getSettingParams().titleSettings().titleMin();
        this.titleStyle = LightEco.getSettingParams().titleSettings().titleStyle();
        this.titleDuration = LightEco.getSettingParams().titleSettings().titleDuration();

        this.soundEnable = LightEco.getSettingParams().titleSettings().soundEnabled();
        this.soundType = Sound.valueOf(LightEco.getSettingParams().titleSettings().soundType().toUpperCase());
        this.soundVolume = LightEco.getSettingParams().titleSettings().soundVolume();
        this.soundChannel = LightEco.getSettingParams().titleSettings().soundChannel();

        this.titleDepositFinalTitle = LightEco.getMessageParams().titleDepositFinalTitle();
        this.titleDepositFinalSubtitle = LightEco.getMessageParams().titleDepositFinalSubtitle();
        this.titleDepositCountTitle = LightEco.getMessageParams().titleDepositCountTitle();
        this.titleDepositCountSubtitle = LightEco.getMessageParams().titleDepositCountSubtitle();

        this.titleWithdrawFinalTitle = LightEco.getMessageParams().titleWithdrawFinalTitle();
        this.titleWithdrawFinalSubtitle = LightEco.getMessageParams().titleWithdrawFinalSubtitle();
        this.titleWithdrawCountTitle = LightEco.getMessageParams().titleWithdrawCountTitle();
        this.titleWithdrawCountSubtitle = LightEco.getMessageParams().titleWithdrawCountSubtitle();

    }

    public void startCount() {
        QueueManager.addToQueue(player, this);
    }

    public void processQueue() {

        if (!titleEnable) {
            return;
        }

        if (amountToCount.doubleValue() < titleMinAmount) {
            return;
        }

        if (player == null || !player.isOnline()) {
            QueueManager.removeQueue(player);
            return;
        }

        EconomyTitle current = QueueManager.getQueue(player).peek();
        if (current == null || current.isCompleted) {
            return;
        }

        BigDecimal increment = current.amountToCount.divide(BigDecimal.valueOf(titleDuration * 20L), RoundingMode.HALF_UP);

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= titleDuration * 20 || current.currentAmount.compareTo(current.amountToCount) >= 0) {
                    current.currentAmount = current.amountToCount; // Ensure currentAmount is exactly amountToCount
                    String finalTitle = current.isDeposit ? titleDepositFinalTitle : titleWithdrawFinalTitle;
                    String finalSubtitle = current.isDeposit ?
                            titleDepositFinalSubtitle.replace("#amount#", NumberFormatter.formatForMessages(current.amountToCount)) :
                            titleWithdrawFinalSubtitle.replace("#amount#", NumberFormatter.formatForMessages(current.amountToCount.negate()));

                    finalTitle = finalTitle.replace("#amount#", NumberFormatter.formatForMessages(current.amountToCount));
                    finalSubtitle = finalSubtitle.replace("#amount#", NumberFormatter.formatForMessages(current.amountToCount));

                    TitleSender.sendTitle(player, finalTitle, finalSubtitle, 0, 40, 40);

                    current.isCompleted = true;
                    this.cancel();
                    QueueManager.getQueue(player).poll();
                    if (!QueueManager.getQueue(player).isEmpty()) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                processQueue();
                            }
                        }.runTaskLater(Light.instance, 80L); // Delay the next queue processing by 40 ticks
                    }
                    return;
                }

                current.currentAmount = current.currentAmount.add(increment);
                if (current.currentAmount.compareTo(current.amountToCount) > 0) {
                    current.currentAmount = current.amountToCount; // Ensure currentAmount does not exceed amountToCount
                }

                String displayTitle = current.isDeposit ? titleDepositCountTitle : titleWithdrawCountTitle;
                String displaySubtitle = current.isDeposit ? titleDepositCountSubtitle : titleWithdrawCountSubtitle;

                displayTitle = displayTitle.replace("#amount#", NumberFormatter.formatForMessages(current.currentAmount));
                displaySubtitle = displaySubtitle.replace("#amount#", NumberFormatter.formatForMessages(current.currentAmount));

                TitleSender.sendTitle(player, displayTitle, displaySubtitle, 0, 40, 40);

                // Play sound based on the current amount (pitch)
                if (soundEnable) {
                    if (ticks % 2 == 0) { // Play sound every 2 ticks
                        if(isDeposit) {
                            player.playSound(player.getLocation(), soundType, soundVolume, maxPitch);
                        } else {
                            player.playSound(player.getLocation(), soundType, soundVolume, minPitch);
                        }
                    }
                }

                ticks++;
            }
        }.runTaskTimer(Light.instance, 0L, 1L);
    }

    public static class Builder {
        private final Player player;
        private BigDecimal amountToCount;
        private boolean isDeposit;

        public Builder(Player player) {
            this.player = player;
        }

        public Builder setAmountToCount(BigDecimal amountToCount) {
            this.amountToCount = amountToCount;
            return this;
        }

        public Builder setIsDeposit(boolean isDeposit) {
            this.isDeposit = isDeposit;
            return this;
        }

        public EconomyTitle build() {
            return new EconomyTitle(this);
        }
    }
}