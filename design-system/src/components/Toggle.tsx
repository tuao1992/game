import React from 'react';

export interface ToggleProps {
  /** Whether the switch is on. */
  checked: boolean;
  /** Called with the next checked value when toggled. */
  onChange?: (checked: boolean) => void;
  /** Optional label shown beside the switch. */
  label?: React.ReactNode;
  /** Disable interaction. @default false */
  disabled?: boolean;
  /** Accessible label when no visible `label` is provided. */
  'aria-label'?: string;
  className?: string;
}

/**
 * On/off pill switch used for the game's Sound, Music and Haptics settings.
 * Grey when off, brand green when on, with a sliding white knob.
 */
export function Toggle({
  checked,
  onChange,
  label,
  disabled = false,
  className = '',
  ...rest
}: ToggleProps) {
  const cls = [
    'wr-toggle',
    checked ? 'wr-toggle--on' : '',
    disabled ? 'wr-toggle--disabled' : '',
    className,
  ]
    .filter(Boolean)
    .join(' ');
  return (
    <label className={cls}>
      <input
        type="checkbox"
        className="wr-toggle__input"
        checked={checked}
        disabled={disabled}
        onChange={(e) => onChange?.(e.target.checked)}
        {...rest}
      />
      <span className="wr-toggle__track" aria-hidden="true">
        <span className="wr-toggle__knob" />
      </span>
      {label != null && <span className="wr-toggle__label">{label}</span>}
    </label>
  );
}
