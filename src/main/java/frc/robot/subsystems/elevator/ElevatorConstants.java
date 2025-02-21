package frc.robot.subsystems.elevator;

import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import frc.lib.generic.Feedforward;
import frc.lib.generic.hardware.motor.*;
import frc.lib.generic.simulation.SimulationProperties;
import frc.lib.generic.visualization.mechanisms.ElevatorMechanism2d;
import frc.lib.generic.visualization.mechanisms.MechanismFactory;
import frc.robot.GlobalConstants;

import static edu.wpi.first.units.Units.Second;
import static edu.wpi.first.units.Units.Volts;
import static frc.robot.GlobalConstants.CURRENT_MODE;
import static frc.robot.utilities.PortsConstants.ElevatorPorts.MASTER_MOTOR_PORT;
import static frc.robot.utilities.PortsConstants.ElevatorPorts.SLAVE_MOTOR_PORT;

public class ElevatorConstants {
    public enum ElevatorHeight {
        L1(0.05),
        L2(0.6),
        L3(2.1),
        FEEDER(0.05),
        CLIMB(-0.1);

        private final double rotations;

        ElevatorHeight(double mechanismRotations) {
            this.rotations = mechanismRotations;
        }

        public double getRotations() {
            return rotations;
        }
    }

    protected static final SysIdRoutine.Config ELEVATOR_CONFIG = new SysIdRoutine.Config(
            Volts.per(Second).of(1),
            Volts.of(2),
            Second.of(7)
    );

    public static final Motor
            MASTER_MOTOR = MotorFactory.createSpark("ELEVATOR_MASTER_MOTOR", MASTER_MOTOR_PORT, MotorProperties.SparkType.FLEX),
            SLAVE_MOTOR = MotorFactory.createSpark("ELEVATOR_SLAVE_MOTOR", SLAVE_MOTOR_PORT, MotorProperties.SparkType.FLEX);

    protected static final double
            ELEVATOR_MAX_EXTENSION_ROTATIONS = 2.2483952045440674,
            ELEVATOR_MIN_EXTENSION_ROTATIONS = -0.0002,
            WHEEL_DIAMETER = 0.0328;

    protected static final ElevatorMechanism2d ELEVATOR_MECHANISM = MechanismFactory.createElevatorMechanism("Elevator Mechanism", 1);

    static {
        configureMotors();
    }

    private static void configureMotors() {
        final MotorConfiguration ELEVATOR_MOTORS_CONFIGURATION = new MotorConfiguration();

        ELEVATOR_MOTORS_CONFIGURATION.forwardSoftLimit = ELEVATOR_MAX_EXTENSION_ROTATIONS;
        ELEVATOR_MOTORS_CONFIGURATION.reverseSoftLimit = ELEVATOR_MIN_EXTENSION_ROTATIONS;

        ELEVATOR_MOTORS_CONFIGURATION.closedLoopTolerance = 0.05;

        ELEVATOR_MOTORS_CONFIGURATION.idleMode = MotorProperties.IdleMode.BRAKE;

        ELEVATOR_MOTORS_CONFIGURATION.profileMaxVelocity = 2;
        ELEVATOR_MOTORS_CONFIGURATION.profileMaxAcceleration = 3;
        ELEVATOR_MOTORS_CONFIGURATION.profileMaxJerk =
                CURRENT_MODE == GlobalConstants.Mode.SIMULATION ? 250 : 25;

        ELEVATOR_MOTORS_CONFIGURATION.supplyCurrentLimit = 50;

        ELEVATOR_MOTORS_CONFIGURATION.inverted = true;
        ELEVATOR_MOTORS_CONFIGURATION.gearRatio = 48;

        ELEVATOR_MOTORS_CONFIGURATION.slot = new MotorProperties.Slot(0.001,0,0,4.9764,0.21689,0.12065, 0.063824, Feedforward.Type.ELEVATOR);

        ELEVATOR_MOTORS_CONFIGURATION.simulationSlot = new MotorProperties.Slot(17.5, 0, 0.6, 0, 0, 0, 0, Feedforward.Type.ELEVATOR);// S=1.313
        ELEVATOR_MOTORS_CONFIGURATION.simulationProperties = new SimulationProperties.Slot(
                SimulationProperties.SimulationType.ELEVATOR,
                DCMotor.getNeoVortex(2),
                1,
                6,
                WHEEL_DIAMETER / 2,
                0.1,
                1.9,
                false
        );

        MASTER_MOTOR.setupSignalUpdates(MotorSignal.VOLTAGE);
        MASTER_MOTOR.setupSignalUpdates(MotorSignal.CURRENT);
        MASTER_MOTOR.setupSignalUpdates(MotorSignal.POSITION);
        MASTER_MOTOR.setupSignalUpdates(MotorSignal.VELOCITY);
        MASTER_MOTOR.setupSignalUpdates(MotorSignal.ACCELERATION);
        MASTER_MOTOR.setupSignalUpdates(MotorSignal.CLOSED_LOOP_TARGET);

        SLAVE_MOTOR.setupSignalUpdates(MotorSignal.VOLTAGE);

        MASTER_MOTOR.configure(ELEVATOR_MOTORS_CONFIGURATION);
        SLAVE_MOTOR.configure(ELEVATOR_MOTORS_CONFIGURATION);

        SLAVE_MOTOR.setFollower(MASTER_MOTOR, true);
    }
}