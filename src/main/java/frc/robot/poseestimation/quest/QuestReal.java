package frc.robot.poseestimation.quest;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Transform2d;
import gg.questnav.questnav.QuestNav;

public class QuestReal extends QuestIO {
    private final QuestNav questNav = new QuestNav();
    private final Transform2d robotToQuest;

    public QuestReal(Transform2d robotToQuest) {
        this.robotToQuest = robotToQuest;
    }

    @Override
    public void setQuestFieldPose(Pose2d robotPose) {
        questNav.setPose(robotPose.transformBy(robotToQuest));
    }

    @Override
    public void updateInputs(QuestIOInputsAutoLogged inputs) {
        inputs.connected = questNav.isConnected();
        inputs.tracking = questNav.isTracking();

        inputs.batteryPercent = questNav.getBatteryPercent();
        inputs.timestamp = questNav.getDataTimestamp();

        inputs.robotPose = questNav.getPose().transformBy(robotToQuest.inverse());

        questNav.commandPeriodic();
    }
}
