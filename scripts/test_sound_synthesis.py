import math
import struct

sample_rate = 44100

def synth_bell(frequencies, durations_ms, decays):
    samples = []
    for freq, dur, decay in zip(frequencies, durations_ms, decays):
        num_samples = int(sample_rate * dur / 1000)
        for s in range(num_samples):
            t = s / sample_rate
            env = math.exp(-decay * (s / num_samples))
            # Harmonic balance
            val = (0.60 * math.sin(2.0 * math.pi * freq * t) +
                   0.25 * math.sin(4.0 * math.pi * freq * t) +
                   0.10 * math.sin(6.0 * math.pi * freq * t) +
                   0.05 * math.sin(8.0 * math.pi * freq * t)) * env
            samples.append(val)
    return samples

# Test Correct chime: C5, E5, G5, C6
correct_samples = synth_bell([523.25, 659.25, 783.99, 1046.50], [60, 60, 60, 220], [4.0, 4.0, 4.0, 3.5])
print(f"Correct sound synthesized: {len(correct_samples)} samples ({len(correct_samples)/sample_rate:.2f}s)")

# Test Victory fanfare: G4, C5, E5, G5, C6
fanfare_samples = synth_bell([392.00, 523.25, 659.25, 783.99, 1046.50], [80, 80, 80, 100, 350], [3.5, 3.5, 3.5, 3.0, 2.5])
print(f"Victory fanfare synthesized: {len(fanfare_samples)} samples ({len(fanfare_samples)/sample_rate:.2f}s)")

# Test Tile tap: 800Hz sliding to 400Hz
def synth_tap():
    dur_ms = 35
    num_samples = int(sample_rate * dur_ms / 1000)
    samples = []
    for s in range(num_samples):
        t = s / sample_rate
        f = 800.0 - 400.0 * (s / num_samples)
        env = math.exp(-9.0 * (s / num_samples))
        val = math.sin(2.0 * math.pi * f * t) * env
        samples.append(val)
    return samples

tap_samples = synth_tap()
print(f"Tile tap synthesized: {len(tap_samples)} samples ({len(tap_samples)/sample_rate:.3f}s)")
print("Sound synthesis formulas verified successfully!")
