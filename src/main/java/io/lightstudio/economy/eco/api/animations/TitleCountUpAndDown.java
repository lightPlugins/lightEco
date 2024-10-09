package io.lightstudio.economy.eco.api.animations;

import io.lightstudio.economy.Light;
import io.lightstudio.economy.eco.LightEco;
import io.lightstudio.economy.util.NumberFormatter;
import io.lightstudio.economy.util.TitleSender;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
public class TitleCountUpAndDown {

    private int fadeIn;
    private int stay;
    private int fadeOut;
    private final int timeToCount = 5;
    private final Player player;
    private BigDecimal amountToCount;
    private BigDecimal currentAmount = BigDecimal.ZERO;
    private boolean isDeposit;
    private boolean isCompleted = false;

    private TitleCountUpAndDown(Builder builder) {
        this.player = builder.player;
        this.amountToCount = builder.amountToCount;
        this.fadeIn = builder.fadeIn;
        this.stay = builder.stay;
        this.fadeOut = builder.fadeOut;
        this.isDeposit = builder.isDeposit;
    }

    public void startCountUp() {
        QueueManager.addToQueue(player, this);
    }

    public void processQueue() {
        if (player == null || !player.isOnline()) {
            QueueManager.removeQueue(player);
            return;
        }

        TitleCountUpAndDown current = QueueManager.getQueue(player).peek();
        if (current == null || current.isCompleted) {
            return;
        }

        BigDecimal increment = current.amountToCount.divide(BigDecimal.valueOf(timeToCount * 20L), RoundingMode.HALF_UP);

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= timeToCount * 20 || current.currentAmount.compareTo(current.amountToCount) >= 0) {
                    current.currentAmount = current.amountToCount; // Ensure currentAmount is exactly amountToCount
                    String finalTitle = current.isDeposit ? LightEco.getMessageParams().titleDepositFinalTitle() : LightEco.getMessageParams().titleWithdrawFinalTitle();
                    String finalSubtitle = current.isDeposit ?
                            LightEco.getMessageParams().titleDepositFinalSubtitle().replace("#amount#", NumberFormatter.formatForMessages(current.amountToCount)) :
                            LightEco.getMessageParams().titleWithdrawFinalSubtitle().replace("#amount#", NumberFormatter.formatForMessages(current.amountToCount.negate()));

                    finalTitle = finalTitle.replace("#amount#", NumberFormatter.formatForMessages(current.amountToCount));
                    finalSubtitle = finalSubtitle.replace("#amount#", NumberFormatter.formatForMessages(current.amountToCount));

                    TitleSender.sendTitle(player, finalTitle, finalSubtitle, 0, 40, 40);
                    current.isCompleted = true;
                    this.cancel();
                    QueueManager.getQueue(player).poll();
                    if (!QueueManager.getQueue(player).isEmpty()) {
                        processQueue();
                    }
                    return;
                }

                current.currentAmount = current.currentAmount.add(increment);
                if (current.currentAmount.compareTo(current.amountToCount) > 0) {
                    current.currentAmount = current.amountToCount; // Ensure currentAmount does not exceed amountToCount
                }

                String displayTitle = current.isDeposit ? LightEco.getMessageParams().titleDepositCountTitle() : LightEco.getMessageParams().titleWithdrawCountTitle();
                String displaySubtitle = current.isDeposit ? LightEco.getMessageParams().titleDepositCountSubtitle() : LightEco.getMessageParams().titleWithdrawCountSubtitle();

                displayTitle = displayTitle.replace("#amount#", NumberFormatter.formatForMessages(current.currentAmount));
                displaySubtitle = displaySubtitle.replace("#amount#", NumberFormatter.formatForMessages(current.currentAmount));

                TitleSender.sendTitle(player, displayTitle, displaySubtitle, 0, 40, 40);
                ticks++;
            }
        }.runTaskTimer(Light.instance, 0L, 1L);
    }

    public static class Builder {
        private final Player player;
        private BigDecimal amountToCount;
        private int fadeIn;
        private int stay;
        private int fadeOut;
        private boolean isDeposit;

        public Builder(Player player) {
            this.player = player;
        }

        public Builder setAmountToCount(BigDecimal amountToCount) {
            this.amountToCount = amountToCount;
            return this;
        }

        public Builder setFadeIn(int fadeIn) {
            this.fadeIn = fadeIn;
            return this;
        }

        public Builder setStay(int stay) {
            this.stay = stay;
            return this;
        }

        public Builder setFadeOut(int fadeOut) {
            this.fadeOut = fadeOut;
            return this;
        }

        public Builder setIsDeposit(boolean isDeposit) {
            this.isDeposit = isDeposit;
            return this;
        }

        public TitleCountUpAndDown build() {
            return new TitleCountUpAndDown(this);
        }
    }
}