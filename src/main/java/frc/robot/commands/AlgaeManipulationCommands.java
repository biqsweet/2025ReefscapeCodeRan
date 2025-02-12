package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.subsystems.algaeblaster.AlgaeBlasterConstants;
import frc.robot.subsystems.algaeintake.AlgaeIntakeConstants;
import frc.robot.subsystems.elevator.ElevatorConstants;
import frc.robot.utilities.FieldConstants;

import static frc.robot.RobotContainer.*;

public class AlgaeManipulationCommands {
    public static Command intakeAlgae() {
        return ALGAE_INTAKE.setAlgaeIntakeState(AlgaeIntakeConstants.IntakeArmState.EXTENDED)
                .raceWith(new WaitCommand(1))
                .andThen(ALGAE_INTAKE.setAlgaeIntakeState(AlgaeIntakeConstants.IntakeArmState.RETRACTED));
    }

    public static Command releaseAlgaeFromIntake() {
        return ALGAE_INTAKE.setAlgaeIntakeState(AlgaeIntakeConstants.IntakeArmState.RETRACTED)
                .andThen(ALGAE_INTAKE.setRollersVoltage(6)
                        .raceWith(new WaitCommand(1.2)));
    }

    /**
     * Sets the elevator to the height of the algae, then removes the algae.
     *
     * @return the command
     */
    public static Command blastAlgaeOffReef(FieldConstants.ReefFace face) {
        return ELEVATOR.setTargetHeight(getAlgaeHeightFromFace(face))
                .alongWith(blastAlgaeOffReef().onlyIf(ELEVATOR::isAtTargetPosition));
    }

    public static Command blastAlgaeOffReef() {
        return ALGAE_BLASTER.setAlgaeBlasterArmState(AlgaeBlasterConstants.BlasterArmState.HORIZONTAL_OUT)
                .andThen(new WaitCommand(0.4))
                .andThen(ALGAE_BLASTER.setAlgaeBlasterArmState(AlgaeBlasterConstants.BlasterArmState.HORIZONTAL_IN));
    }

    private static ElevatorConstants.ElevatorHeight getAlgaeHeightFromFace(FieldConstants.ReefFace face) {
        if (face.ordinal() % 2 == 0) return ElevatorConstants.ElevatorHeight.L2;
        return ElevatorConstants.ElevatorHeight.L1;
    }
}
