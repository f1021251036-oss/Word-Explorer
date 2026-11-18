import type { CSSProperties, ReactNode } from 'react';
import { CheckmarkIcon } from './ExpeditionIcons';

interface ParchmentDossierCardProps {
  children: ReactNode;
  className?: string;
  style?: CSSProperties;
}

export function ParchmentDossierCard({
  children,
  className = '',
  style,
}: ParchmentDossierCardProps) {
  return (
    <section className={`dossier-card ${className}`.trim()} style={style}>
      <span className="dossier-crosshair dossier-crosshair--tl" aria-hidden="true" />
      <span className="dossier-crosshair dossier-crosshair--tr" aria-hidden="true" />
      <span className="dossier-crosshair dossier-crosshair--bl" aria-hidden="true" />
      <span className="dossier-crosshair dossier-crosshair--br" aria-hidden="true" />
      {children}
    </section>
  );
}

interface WaxSealBadgeProps {
  label?: string;
  icon?: ReactNode;
  size?: number;
  className?: string;
}

export function WaxSealBadge({
  label = '',
  icon,
  size = 32,
  className = '',
}: WaxSealBadgeProps) {
  return (
    <span
      className={`wax-seal ${className}`.trim()}
      style={{ width: size, height: size, minWidth: size, minHeight: size }}
      aria-hidden={label ? undefined : true}
    >
      {icon ?? <span>{label}</span>}
    </span>
  );
}

interface HandDrawnWindingTrailProps {
  completedSteps: number;
  totalSteps: number;
  className?: string;
}

export function HandDrawnWindingTrail({
  completedSteps,
  totalSteps,
  className = '',
}: HandDrawnWindingTrailProps) {
  const count = Math.max(1, totalSteps);
  const clamped = Math.max(0, Math.min(count, completedSteps));

  return (
    <div className={`winding-trail ${className}`.trim()} role="img" aria-label={`${clamped} dari ${count} tahap selesai`}>
      <div className="winding-trail__line" aria-hidden="true" />
      <div
        className="winding-trail__line-active"
        style={{
          width: count > 1 ? `${(Math.min(clamped, count - 1) / (count - 1)) * 100}%` : '100%',
        }}
        aria-hidden="true"
      />
      <div className="winding-trail__dots">
        {Array.from({ length: count }, (_, idx) => {
          const isCompleted = idx < clamped;
          const isCurrent = idx === clamped && clamped < count;
          return (
            <span
              key={idx}
              className={`winding-trail__dot ${
                isCompleted ? 'is-completed' : isCurrent ? 'is-current' : ''
              }`}
            >
              {isCompleted && <CheckmarkIcon size={10} />}
            </span>
          );
        })}
      </div>
    </div>
  );
}

interface TactileSignpostButtonProps {
  label: string;
  subLabel?: string;
  icon?: ReactNode;
  onClick: () => void;
  disabled?: boolean;
  variant?: 'forest' | 'terracotta';
  testTag?: string;
  className?: string;
}

export function TactileSignpostButton({
  label,
  subLabel,
  icon,
  onClick,
  disabled = false,
  variant = 'forest',
  testTag,
  className = '',
}: TactileSignpostButtonProps) {
  return (
    <button
      type="button"
      className={`signpost-btn signpost-btn--${variant} ${className}`.trim()}
      onClick={onClick}
      disabled={disabled}
      data-testid={testTag}
    >
      {icon && <span className="signpost-btn__icon">{icon}</span>}
      <span className="signpost-btn__copy">
        <span className="signpost-btn__label">{label}</span>
        {subLabel && <span className="signpost-btn__sub">{subLabel}</span>}
      </span>
    </button>
  );
}
