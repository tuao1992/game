import React from 'react';
import { Toggle } from '@weldrite/design-system';

const Stage = ({ children }: { children: React.ReactNode }) => (
  <div style={{ background: '#0e2a47', padding: 28, display: 'flex', flexDirection: 'column', gap: 18 }}>
    {children}
  </div>
);

export const Settings = () => {
  const [sound, setSound] = React.useState(true);
  const [music, setMusic] = React.useState(false);
  const [haptics, setHaptics] = React.useState(true);
  return (
    <Stage>
      <Toggle checked={sound} onChange={setSound} label="Sound" />
      <Toggle checked={music} onChange={setMusic} label="Music" />
      <Toggle checked={haptics} onChange={setHaptics} label="Haptics" />
    </Stage>
  );
};

export const States = () => (
  <Stage>
    <Toggle checked={false} label="Off" />
    <Toggle checked={true} label="On" />
    <Toggle checked={true} disabled label="Disabled" />
  </Stage>
);
