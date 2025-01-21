package frc.robot.subsystems.elevator;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.lib.generic.GenericSubsystem;
import frc.lib.generic.hardware.motor.MotorProperties;
import frc.lib.math.Conversions;
import org.littletonrobotics.junction.Logger;

import static frc.robot.GlobalConstants.CURRENT_MODE;
import static frc.robot.GlobalConstants.Mode.REAL;
import static frc.robot.subsystems.elevator.ElevatorConstants.*;

public class Elevator extends GenericSubsystem {
    public Command setTargetPosition(ElevatorHeight level){
        return Commands.runEnd(() -> {
            setMotorPosition(level.rotations);
            if (CURRENT_MODE != REAL) printPose(level.meters);
        }, this::stopMotors, this);
    }

    public void periodic() {
        if (BOTTOM_BEAM_BREAK.get() == 1)
            MASTER_MOTOR.setMotorEncoderPosition(0);

        if (TOP_BEAM_BREAK.get() == 1)
            MASTER_MOTOR.setMotorEncoderPosition(ELEVATOR_MAX_EXTENSION_METERS);
    }

    private void printPose(double targetPositionMeters) {
        final Pose3d current3DPose = new Pose3d(0, 0, Conversions.rotationsToMetres(MASTER_MOTOR.getSystemPosition(),WHEEL_DIAMETER), new Rotation3d(0, 0, 0));
        Logger.recordOutput("Elevator", current3DPose);

        ELEVATOR_MECHANISM.updateCurrentPosition(Conversions.rotationsToMetres(MASTER_MOTOR.getSystemPosition(), WHEEL_DIAMETER));
        ELEVATOR_MECHANISM.updateTargetPosition(targetPositionMeters);
    }

    private void setMotorPosition(double targetPosition) {
        MASTER_MOTOR.setOutput(MotorProperties.ControlMode.POSITION, targetPosition);
    }

    private void stopMotors() {
        MASTER_MOTOR.stopMotor();
    }
}
