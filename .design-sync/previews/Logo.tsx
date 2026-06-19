import React from 'react';
import { Logo } from '@weldrite/design-system';

const Stage = ({ children }: { children: React.ReactNode }) => (
  <div style={{ background: '#0e2a47', padding: 36, display: 'flex', justifyContent: 'center' }}>{children}</div>
);

export const Primary = () => (
  <Stage>
    <Logo size={72} />
  </Stage>
);

export const Wordmark = () => (
  <Stage>
    <Logo size={64} showSubtitle={false} />
  </Stage>
);

export const Small = () => (
  <Stage>
    <Logo size={40} />
  </Stage>
);
