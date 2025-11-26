/** @jsxImportSource @emotion/react */
import React, { useState } from 'react'
import { css } from "@emotion/react";
import Header from './Header';
import Sidebar from './Sidebar';
import Footer from './Footer';

function Layout({ children }: { children: React.ReactNode }) {
  const [sidebarOpen, setSidebarOpen] = useState<boolean>(false);

  const handleToggleSidebar = () => {
    setSidebarOpen(prev => !prev);
  }

  const handleCloseSidebar = () => {
    setSidebarOpen(false);
  }

  return (
    <div css={layoutStyle}>
      <Header onToggleSidebar={handleToggleSidebar}/>
      <div css={contentStyle}>
        <Sidebar isOpen={sidebarOpen} onClose={handleCloseSidebar}/>
          <main css={mainStyle}>{children}</main>
        <Footer />
      </div>
    </div>
  )
}

export default Layout

const layoutStyle = css`
  display: flex;
  flex-direction: column;
  height: 100vh;
`;

const contentStyle = css`
  flex: 1;
  position: relative;
  display: flex;
  flex-direction: row;
  overflow: hidden;
  transition: all 0.25s ease;
`;

const mainStyle = css`
  flex: 1;
  flex-direction: column;
  overflow-y: auto;
  padding: 1.5rem;
  display: flex;
  gap: 1rem;
`;